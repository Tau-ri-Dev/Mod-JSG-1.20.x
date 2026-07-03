#version 150

// Procedural Stargate event horizon. The flat disc mesh (InnerCircle + QuadStrips) is displaced here in
// the vertex stage by the SAME height field the fragment shader uses for its caustics, so the physical
// 3D waves and the lit surface match. Displacement is along the disc's facing normal (passed in, because
// the gate pose is baked into Position). UV0 carries the normalised disc coordinate.

in vec3 Position;
in vec2 UV0;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float Time;
uniform vec3 SurfaceNormal;   // disc facing direction, same space as Position

out vec2 discCoord;
out vec4 vColor;

const float WAVE_AMPLITUDE = 0.35;
const float WAVE_MEAN = 0.75;   // approximate mean of waveHeight, so displacement is centred

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 345.45));
    p += dot(p, p + 34.345);
    return fract(p.x * p.y);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * f * (f * (f * 6.0 - 15.0) + 10.0);
    return mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), u.x),
               mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), u.x), u.y);
}

float fbm(vec2 p) {
    float v = 0.0;
    float a = 0.5;
    mat2 rot = mat2(0.80, 0.60, -0.60, 0.80);
    for (int i = 0; i < 5; i++) {
        v += a * noise(p);
        p = rot * p * 2.0 + vec2(7.1, 3.7);
        a *= 0.5;
    }
    return v;
}

// Must match the fragment shader's waveHeight().
float waveHeight(vec2 p, float t) {
    float c = smoothstep(1.0, 0.0, length(p));   // 1 at centre, 0 at rim
    float h = (0.70 + 0.60 * c) * fbm(p * 1.0 + vec2(t * 0.13, -t * 0.09));
    h += (0.35 + 0.20 * c) * fbm(p * 2.2 + vec2(-t * 0.11, t * 0.12));
    h += 0.20 * c * fbm(p * 4.0 + vec2(t * 0.18, t * 0.16));
    return h;
}

void main() {
    vec2 disc = UV0 * 2.0 - 1.0;
    float r = length(disc);

    // 3D wave displacement along the disc normal, tapered so the rim stays flush with the ring.
    float disp = (waveHeight(disc, Time) - WAVE_MEAN) * WAVE_AMPLITUDE * smoothstep(1.0, 0.85, r);
    vec3 pos = Position + SurfaceNormal * disp;

    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);
    discCoord = disc;
    vColor = Color;
}
