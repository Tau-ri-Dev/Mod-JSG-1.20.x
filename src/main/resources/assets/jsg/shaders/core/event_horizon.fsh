#version 150

// Stargate event horizon: a deep-blue liquid surface that ripples in place (no rotation). Large slow
// waves form a height field; a directional light catches their slopes as bright blue-white specular
// streaks with a soft bloom-like halo, over a glowing blue core. Opaque to the ring, tinted per gate.

in vec2 discCoord;
in vec4 vColor;

uniform vec4 ColorModulator;
uniform float Time;
uniform float FillProgress;  // 0 = empty (closed), 1 = puddle fully covers the ring (grows inward from rim)
uniform float WhiteAmount;   // 0 = settled water, 1 = bright white splash (formation / flash)

out vec4 fragColor;

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 345.45));
    p += dot(p, p + 34.345);
    return fract(p.x * p.y);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    // Quintic fade (C2-continuous) — smooth second derivative so finite-difference normals don't show
    // the grid as blocky/square highlights.
    vec2 u = f * f * f * (f * (f * 6.0 - 15.0) + 10.0);
    return mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), u.x),
               mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), u.x), u.y);
}

float fbm(vec2 p) {
    float v = 0.0;
    float a = 0.5;
    // Rotate the domain each octave so the per-octave value-noise grids don't align — kills the
    // axis-aligned "square" structure and reads as organic water.
    mat2 rot = mat2(0.80, 0.60, -0.60, 0.80);
    for (int i = 0; i < 5; i++) {
        v += a * noise(p);
        p = rot * p * 2.0 + vec2(7.1, 3.7);
        a *= 0.5;
    }
    return v;
}

// Worley (cellular) F1: distance to the nearest jittered feature point (one per cell). Unlike fbm this has
// scattered LOCAL MINIMA, so thresholding it reveals disjoint blobs that spawn at points and scale outward
// (a smooth field can only sweep one connected front) — the key to the show's establish character.
float worley(vec2 p, float cells) {
    vec2 s = p * cells;
    vec2 g = floor(s);
    float md = 9.9;
    for (int oy = -1; oy <= 1; oy++) {
        for (int ox = -1; ox <= 1; ox++) {
            vec2 cell = g + vec2(float(ox), float(oy));
            vec2 jit = vec2(hash(cell), hash(cell + vec2(37.2, 17.1)));
            md = min(md, length(cell + jit - s));
        }
    }
    return md;
}

// Surface height. A big, slow base wave everywhere (bigger caustics), then medium + fine octaves whose
// amplitude is weighted toward the centre — so detail packs in densely at the middle and thins out to
// just the large smooth waves at the rim (sparser). No coordinate warping, so features never stretch.
float waveHeight(vec2 p, float t) {
    float c = smoothstep(1.0, 0.0, length(p));   // 1 at centre, 0 at rim
    // Big features dominate and grow bolder toward the centre (bigger caustics there), a medium octave
    // adds density, and everything eases down toward the rim (sparser, calmer).
    float h = (0.70 + 0.60 * c) * fbm(p * 1.0 + vec2(t * 0.13, -t * 0.09));
    h += (0.35 + 0.20 * c) * fbm(p * 2.2 + vec2(-t * 0.11, t * 0.12));
    h += 0.20 * c * fbm(p * 4.0 + vec2(t * 0.18, t * 0.16));
    return h;
}

void main() {
    float r = length(discCoord);
    if (r > 1.0) discard;

    // Establish fill — fitted procedurally to the show's establish frames (their per-pixel fill order was
    // extracted offline and these weights fitted to it, then verified against an 18-frame render of this
    // exact model). Character: ragged LEFT/RIGHT arcs that sweep inward with the centre filling last, broken
    // into DISJOINT blobs that spawn off the edges and scale outward. Worley (scattered minima) supplies the
    // blobs, the |x|/radius bias sweeps them in as arcs, +x makes the LEFT lead, and a domain warp churns the
    // blob/arc edges. Static (no Time) so the blobs grow in place rather than drifting.
    vec2 warp = vec2(fbm(discCoord * 2.0), fbm(discCoord * 2.0 + vec2(5.3, -2.1))) - 0.5;
    float blob = worley(discCoord + 0.55 * warp, 3.0);
    float rawOrder = 0.70 * blob
                   + 0.58 * (1.0 - abs(discCoord.x))
                   + 0.42 * (1.0 - r)
                   + 0.45 * (fbm(discCoord * 6.0) - 0.5)
                   + 0.14 * discCoord.x;
    // Normalise the model's measured range (LO=-0.182, span=1.655) to [0, 0.92] so every disc pixel fills a
    // bit before the end (no lingering holes that snap shut at the guard); fillThresh = FillProgress sweeps it.
    float fillOrder = clamp((rawOrder + 0.182) / 1.655, 0.0, 1.0) * 0.92;
    float fillThresh = FillProgress;
    if (FillProgress < 0.99 && fillOrder > fillThresh) discard;

    float t = Time;
    vec2 p = discCoord;   // no rotation — the puddle ripples, it does not spin

    // Surface normal from the height field (finite differences). The slope exaggeration ramps up while the
    // puddle is establishing (WhiteAmount high) so the surface churns violently, then settles to the calm
    // 0.7 ripple as it stabilises — like the show's establish.
    float turb = clamp(WhiteAmount * 1.3, 0.0, 1.0);
    float slope = mix(0.7, 2.6, turb);
    float e = 0.02;
    float hC = waveHeight(p, t);
    float hX = waveHeight(p + vec2(e, 0.0), t);
    float hY = waveHeight(p + vec2(0.0, e), t);
    vec3 N = normalize(vec3((hC - hX) / e * slope, (hC - hY) / e * slope, 1.0));

    // Directional half-vector specular: broad streaks, sharp crest sparkle, and a wide soft halo (bloom).
    vec3 V = vec3(0.0, 0.0, 1.0);
    vec3 L = normalize(vec3(0.25, 0.50, 0.80));
    vec3 Hh = normalize(L + V);
    float ndh = max(dot(N, Hh), 0.0);
    // Multiple specular lobes, sharp to very broad. The broad lobes are the in-shader "bloom": each
    // bright crest carries a soft glow that bleeds into the gaps, so neighbouring caustics merge — and
    // the bleed grows toward the centre.
    float bloom = pow(smoothstep(1.0, 0.0, r), 1.4);
    float spec = pow(ndh, 26.0) * 0.50                     // sharp sparkle
               + pow(ndh, 9.0)  * 0.85                     // main crests
               + pow(ndh, 4.0)  * (0.65 + 0.70 * bloom)    // soft glow, stronger toward centre
               + pow(ndh, 2.0)  * (0.40 + 1.00 * bloom);   // wide bleed, stronger toward centre

    // Radial profile: 1 at the centre, 0 at the rim. Highlights concentrate toward the centre so the
    // surface reads as a bright core fading to plain deep blue at the ring — not glowing white all over.
    float center = smoothstep(1.0, 0.0, r);
    // Concentrate highlights toward the centre, but keep a floor so the rim ripples stay faintly visible
    // (the outer ring shouldn't be dead-empty deep blue).
    float specMask = mix(0.10, 1.0, pow(center, 1.6));

    // Deep-blue body — bluer than before and never black.
    vec3 tint = vColor.rgb;
    vec3 deep = tint * 0.30 + vec3(0.01, 0.03, 0.10);
    vec3 blue = tint * 1.0;
    vec3 col = mix(deep, blue, center * 0.50 + 0.12);

    // Bright blue-white caustics, masked toward the centre so the rim stays deep blue. The multi-lobe
    // spec already blooms each highlight into its neighbours, so they connect and merge.
    vec3 hi = mix(tint, vec3(0.82, 0.91, 1.0), 0.62);
    col += hi * spec * specMask;

    // Gentle central blue lift — no hard white disc; the merged caustics make the centre glow.
    col += vec3(0.40, 0.62, 1.0) * pow(center, 2.5) * 0.45;

    // White foam: the freshly-established blotches glow bright white (the energy front), fading to water
    // deeper in; plus the overall white flash (WhiteAmount). The fresh-edge white fades out as the disc
    // finishes filling so the settled puddle is clean water.
    float foam = 0.70 + 0.30 * fbm(discCoord * 5.0 - vec2(Time * 0.7, Time * 0.5));
    float establishing = smoothstep(1.0, 0.85, FillProgress);          // 1 while filling, 0 when full
    float edgeProx = clamp((fillThresh - fillOrder) / 0.40, 0.0, 1.0); // 0 at the fresh fill edge -> 1 deep inside
    float whiteAmt = clamp(max(WhiteAmount * foam, (1.0 - edgeProx) * establishing), 0.0, 1.0);
    col = mix(col, vec3(1.0), whiteAmt);

    // Opaque to the rim (sliver of anti-aliasing at the very edge).
    float edge = smoothstep(1.0, 0.985, r);

    col *= ColorModulator.rgb;
    fragColor = vec4(col, edge * ColorModulator.a);
}
