#version 150

// Volumetric kawoosh — proxy bounding box. Position is the box corner in GATE-LOCAL space (x,y across the
// gate mouth, z forward along the burst axis); it is NOT pose-baked, so the fragment shader can raymarch
// in local space. ModelMat applies the gate's pose (camera-relative), ModelViewMat the camera.

in vec3 Position;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 ModelMat;

out vec3 vLocalPos;
out vec4 vColor;

void main() {
    vLocalPos = Position;
    gl_Position = ProjMat * ModelViewMat * ModelMat * vec4(Position, 1.0);
    vColor = Color;
}
