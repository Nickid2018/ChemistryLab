#version 130

in vec2 outTexturePos;
in vec4 outSpriteColor;

flat in int outTexturePage;

out vec4 finalColor;

uniform sampler2D textureSimpler[16];

void main() { 
  vec4 pixelColor = texture2D(textureSimpler[outTexturePage], outTexturePos);
  finalColor = mix(outSpriteColor, pixelColor, 0.6);
};