#version 150

// Volumetric kawoosh. For each pixel of the proxy box we reconstruct the view ray in gate-local space,
// clip it to the plume's bounding box, and raymarch a 3D-FBM density field shaped as a turbulent plume
// bursting from the gate mouth (z = 0) forward to z = PlumeLength * Progress, compositing front-to-back.

in vec3 vLocalPos;     // fragment position in gate-local space (on the proxy box)
in vec4 vColor;        // per-gate tint

uniform vec3 CameraLocal;   // camera position in gate-local space
uniform float Time;
uniform float Progress;     // 0..1 burst extension
uniform float PlumeLength;
uniform float PlumeRadius;
uniform vec4 ColorModulator;
uniform mat4 ModelMat;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 fragColor;

float hash(vec3 p) {
    p = fract(p * 0.3183099 + 0.1);
    p *= 17.0;
    return fract(p.x * p.y * p.z * (p.x + p.y + p.z));
}

float noise(vec3 x) {
    vec3 i = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(mix(hash(i + vec3(0, 0, 0)), hash(i + vec3(1, 0, 0)), f.x),
                   mix(hash(i + vec3(0, 1, 0)), hash(i + vec3(1, 1, 0)), f.x), f.y),
               mix(mix(hash(i + vec3(0, 0, 1)), hash(i + vec3(1, 0, 1)), f.x),
                   mix(hash(i + vec3(0, 1, 1)), hash(i + vec3(1, 1, 1)), f.x), f.y), f.z);
}

float fbm(vec3 p) {
    float v = 0.0;
    float a = 0.5;
    // 4 octaves — finer churning detail (3D noise is expensive: this runs per raymarch step per pixel).
    for (int i = 0; i < 4; i++) {
        v += a * noise(p);
        p = p * 2.0 + vec3(11.1, 3.7, 5.3);
        a *= 0.5;
    }
    return v;
}

// Plume density at a gate-local point q.
float density(vec3 q, float L) {
    if (q.z < 0.0 || q.z > L) return 0.0;
    float zn = q.z / max(L, 0.001);                 // 0 at gate mouth, 1 at front
    // Burst shape: wide at the gate mouth, staying broad down its length and capping in a wide, soft front
    // (not tapering to a point) so it reads as a billowing column, not a cone/bulb.
    float prof = PlumeRadius * (0.95 + 0.35 * zn) * (1.0 - smoothstep(0.82, 1.0, zn) * 0.50);
    float rr = length(q.xy);
    float shell = clamp(1.0 - rr / max(prof, 0.001), 0.0, 1.0);  // 1 at the axis -> 0 at the profile radius
    if (shell <= 0.001) return 0.0;
    // Erode the smooth shape with fbm to carve billowing, cauliflower cloud lumps (volumetric-cloud look)
    // rather than a smooth blob — this is what reads as churning water-splash structure.
    vec3 fp = q * 1.55;
    fp.z += Time * 1.8;     // churn always flows back toward the gate, slowly
    float n = fbm(fp);
    float d = smoothstep(0.0, 0.35, shell - (1.0 - n) * 0.72);   // harder erosion -> more broken, lumpy churn
    d *= smoothstep(1.0, 0.80, zn);                 // soft front (longer fade -> no hard front clip)
    return d;
}

// Ray vs axis-aligned plume box (x,y in [-R,R], z in [0,L]).
bool intersectBox(vec3 ro, vec3 rd, float R, float L, out float t0, out float t1) {
    vec3 inv = 1.0 / rd;
    vec3 ta = (vec3(-R, -R, 0.0) - ro) * inv;
    vec3 tb = (vec3(R, R, L) - ro) * inv;
    vec3 tmin = min(ta, tb);
    vec3 tmax = max(ta, tb);
    t0 = max(max(tmin.x, tmin.y), tmin.z);
    t1 = min(min(tmax.x, tmax.y), tmax.z);
    return t1 > max(t0, 0.0);
}

// Per-pixel hash for ray-start dithering (breaks raymarch banding into fine noise).
float hash21(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

void main() {
    if (gl_FrontFacing) discard;   // box proxy: keep only back faces -> one ray per pixel
    float L = PlumeLength * Progress;
    if (L < 0.05) discard;

    vec3 ro = CameraLocal;
    vec3 rd = normalize(vLocalPos - CameraLocal);

    float t0, t1;
    if (!intersectBox(ro, rd, PlumeRadius * 1.3, L, t0, t1)) discard;   // box wider than the radius profile (no side clip)
    t0 = max(t0, 0.0);

    const int STEPS = 36;
    float dt = (t1 - t0) / float(STEPS);
    float t = t0 + dt * hash21(gl_FragCoord.xy);   // per-pixel dither -> breaks banding into fine noise

    // Lit like the reference: a glowing blue-white mass. Self-shadowing gives 3D form (deep blue recesses
    // -> bright blue-white lit foam), and the event horizon backlights the base of the plume in blue.
    // White foam, with the blue appearing only as shading in the self-shadowed recesses.
    vec3 gate      = vColor.rgb;
    vec3 litCol    = vec3(0.95, 0.97, 1.0);                       // lit foam: white (faint cool tint)
    vec3 shadowCol = mix(vec3(0.52, 0.61, 0.76), gate, 0.28);     // recess shading: muted grey-blue (not vivid)
    vec3 Ldir = normalize(vec3(0.30, 0.85, -0.40));

    float alpha = 0.0;
    vec3 col = vec3(0.0);
    bool hasHit = false;
    vec3 hitPos = vec3(0.0);
    for (int i = 0; i < STEPS; i++) {
        vec3 q = ro + rd * t;
        float d = density(q, L);
        if (d > 0.001) {
            if (!hasHit && d > 0.05) { hasHit = true; hitPos = q; }   // front surface -> depth write
            // Per-lump shading from the density gradient: every small bump is lit on one side and shaded
            // (blue) on the other, so the blue shading sits across the whole surface, not just big recesses.
            float e = 0.18;
            vec3 grad = vec3(density(q + vec3(e, 0.0, 0.0), L),
                             density(q + vec3(0.0, e, 0.0), L),
                             density(q + vec3(0.0, 0.0, e), L)) - vec3(d);
            vec3 nrm = normalize(-grad + vec3(1e-4));
            float shade = dot(nrm, Ldir) * 0.55 + 0.38;          // biased toward shade -> more blue overall
            shade *= (1.0 - 0.42 * alpha);                       // interior/far side bluer (depth)
            vec3 sc = mix(shadowCol, litCol, clamp(shade, 0.0, 1.0));
            // Event-horizon backlight: a gentle blue glow strongest near the gate mouth, fading forward.
            float glow = smoothstep(L * 0.65, 0.0, q.z);
            sc += gate * glow * 0.16;
            float a = d * 1.25;
            col += (1.0 - alpha) * sc * a * 1.7;
            alpha += (1.0 - alpha) * a;
            if (alpha > 0.99) break;
        }
        t += dt;
    }

    if (alpha < 0.01) discard;
    col *= ColorModulator.rgb;
    fragColor = vec4(col, alpha * ColorModulator.a);

    // Write the volume's front-surface depth so later geometry (vanilla clouds) is correctly occluded.
    if (hasHit) {
        vec4 clip = ProjMat * ModelViewMat * ModelMat * vec4(hitPos, 1.0);
        gl_FragDepth = clip.z / clip.w * 0.5 + 0.5;
    } else {
        gl_FragDepth = gl_FragCoord.z;
    }
}
