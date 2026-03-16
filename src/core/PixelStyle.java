package core;

public enum PixelStyle {
	BIT_8(256, 144),
	BIT_16(320, 180),
	BIT_32(480, 270),
	BIT_64(640, 360),
	BIT_128(960, 540),
	BIT_256(1920, 1080);

	public final int virtualWidth;
	public final int virtualHeight;

	PixelStyle(int virtualWidth, int virtualHeight) {
		this.virtualWidth = virtualWidth;
		this.virtualHeight = virtualHeight;
	}
}
