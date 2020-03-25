#version 130

in vec2 vertexPos;
in vec2 texturePos;
in int texturePage;
in vec4 spriteColor;

out vec2 outTexturePos;
out vec4 outSpriteColor;

flat out int outTexturePage;

void main() {
	gl_Position = vec4(vertexPos, 0, 1);
	outTexturePos = texturePos;
	outTexturePage = texturePage;
	outSpriteColor = spriteColor;
};