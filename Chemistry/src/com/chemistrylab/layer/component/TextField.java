package com.chemistrylab.layer.component;

import java.io.*;
import org.lwjgl.glfw.*;
import java.awt.Toolkit;
import java.util.function.*;
import com.chemistrylab.*;
import org.newdawn.slick.*;
import java.awt.datatransfer.*;
import com.chemistrylab.layer.*;
import com.chemistrylab.render.*;
import com.chemistrylab.util.Mouse;

import static org.lwjgl.opengl.GL11.*;

public class TextField extends Component {

	private String pa;
	private int size;
	private int startpaint;
	private int postion = 0;
	private int selpostionstart = 0;
	private int selpostionend = 0;
	private int start_to_sel = 0;
	private Color color = Color.white;
	private Color selcolor = new Color(20, 20, 255, 150);
	private Consumer<String> fire_enter;

	private static final Clipboard CLIP = Toolkit.getDefaultToolkit().getSystemClipboard();

	public TextField(float x0, float x1, float y0, float y1, Layer l, int size) {
		this(x0, x1, y0, y1, l, size, "");
	}

	public TextField(float x0, float x1, float y0, float y1, Layer l, int size, String s) {
		super(x0, x1, y0, y1, l);
		pa = s;
		this.size = size;
		startpaint = 0;
	}

	public String getString() {
		return pa;
	}

	public void setString(String pa) {
		this.pa = pa;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPostion() {
		return postion;
	}

	public void setPostion(int postion) {
		this.postion = postion;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Consumer<String> getEnterEvent() {
		return fire_enter;
	}

	public void setEnterEvent(Consumer<String> fire_enter) {
		this.fire_enter = fire_enter;
	}

	private boolean focus_on = false;
	private boolean started = false;
	private boolean shift_on = false;
	private boolean started_split = false;
	private long startTime;

	@Override
	public synchronized void onMouseEvent(int button, int action, int mods) {
		if (button != 0)
			return;
		focus_on = action == GLFW.GLFW_PRESS;
		if (focus_on) {
			startTime = ChemistryLab.getTime();
		} else {
			started = ChemistryLab.getTime() - startTime > 100;
			if (started)
				started_split = true;
			else {
				float len = CommonRender.winToOthWidth((float) Mouse.getX());
				String s = CommonRender.subTextWidth(pa.substring(startpaint), size, len - range.x0);
				postion = startpaint + s.length();
				selpostionstart = selpostionend = start_to_sel = 0;
			}
		}
	}

	@Override
	public void onCursorPositionChanged(double xpos, double ypos) {
		if (!focus_on)
			return;
		float len = CommonRender.winToOthWidth((float) xpos);
		String s = CommonRender.subTextWidth(pa.substring(startpaint), size, len - range.x0);
		postion = startpaint + s.length();
		if (focus_on) {
			if (started && !started_split) {
				if (postion >= start_to_sel) {
					selpostionstart = start_to_sel;
					selpostionend = postion;
				} else {
					selpostionstart = postion;
					selpostionend = start_to_sel;
				}
			} else {
				started = true;
				started_split = false;
				start_to_sel = selpostionstart = selpostionend = postion;
			}
		}
	}

	private boolean modPress(int mods, int key) {
		return (mods & key) == key;
	}

	@Override
	public void onKeyActive(int key, int scancode, int action, int mods) {
		if (action == GLFW.GLFW_RELEASE)
			return;
		// shift
		if ((key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT)
				|| modPress(mods, GLFW.GLFW_MOD_SHIFT)) {
			shift_on = true;
		} else {
			shift_on = false;
		}
		// Ctrl+A select all
		if (modPress(mods, GLFW.GLFW_MOD_CONTROL) && key == GLFW.GLFW_KEY_A) {
			selpostionstart = 0;
			selpostionend = pa.length();
			return;
		}
		int tmp = postion;
		// left
		if (key == GLFW.GLFW_KEY_LEFT) {
			if (postion == startpaint)
				startpaint = startpaint >= 1 ? startpaint - 1 : startpaint;
			postion = postion >= 1 ? postion - 1 : postion;
			if (shift_on) {
				if (started) {
					if (postion >= start_to_sel) {
						selpostionstart = start_to_sel;
						selpostionend = postion;
					} else {
						selpostionstart = postion;
						selpostionend = start_to_sel;
					}
				} else {
					started = true;
					start_to_sel = selpostionstart = selpostionend = tmp;
				}
			} else {
				started = false;
				start_to_sel = selpostionstart = selpostionend = 0;
			}
			if (postion < startpaint) {
				String sub = pa.substring(postion);
				int lim = CommonRender.subTextWidth(sub, size, range.x1 - range.x0).length();
				if (lim == sub.length()) {
					StringBuilder bi = new StringBuilder(pa);
					startpaint = pa.length()
							- CommonRender.subTextWidth(bi.reverse().toString(), size, range.x1 - range.x0).length();
				} else {
					startpaint = postion;
				}
			}
			return;
		}
		// right
		if (key == GLFW.GLFW_KEY_RIGHT) {
			if (postion == startpaint + CommonRender.subTextWidth(pa.substring(startpaint), size, range.x1).length())
				startpaint = startpaint <= pa.length() - 1 ? startpaint + 1 : startpaint;
			postion = postion <= pa.length() - 1 ? postion + 1 : postion;
			if (shift_on) {
				if (started) {
					if (postion >= start_to_sel) {
						selpostionstart = start_to_sel;
						selpostionend = postion;
					} else {
						selpostionstart = postion;
						selpostionend = start_to_sel;
					}
				} else {
					started = true;
					start_to_sel = selpostionstart = selpostionend = tmp;
				}
			} else {
				started = false;
				start_to_sel = selpostionstart = selpostionend = 0;
			}
			if (postion > startpaint) {
				String todeal = pa.substring(0, postion);
				StringBuilder bi = new StringBuilder(todeal);
				startpaint = postion
						- CommonRender.subTextWidth(bi.reverse().toString(), size, range.x1 - range.x0).length();
			}
			return;
		}
		// Ctrl+C copy
		if (modPress(mods, GLFW.GLFW_MOD_CONTROL) && key == GLFW.GLFW_KEY_C) {
			if (selpostionstart != selpostionend) {
				Transferable trans = new StringSelection(pa.substring(selpostionstart, selpostionend));
				CLIP.setContents(trans, null);
			}
			return;
		}
		// Ctrl+X cut
		if (modPress(mods, GLFW.GLFW_MOD_CONTROL) && key == GLFW.GLFW_KEY_X) {
			if (selpostionstart != selpostionend) {
				Transferable trans = new StringSelection(pa.substring(selpostionstart, selpostionend));
				CLIP.setContents(trans, null);
				pa = pa.substring(0, selpostionstart) + pa.substring(selpostionend);
				postion = selpostionend = selpostionstart;
			}
			if (postion < startpaint) {
				String sub = pa.substring(postion);
				int lim = CommonRender.subTextWidth(sub, size, range.x1 - range.x0).length();
				if (lim == sub.length()) {
					StringBuilder bi = new StringBuilder(pa);
					startpaint = pa.length()
							- CommonRender.subTextWidth(bi.reverse().toString(), size, range.x1 - range.x0).length();
				} else {
					startpaint = postion;
				}
			}
			return;
		}
		// Ctrl+V paste
		if (modPress(mods, GLFW.GLFW_MOD_CONTROL) && key == GLFW.GLFW_KEY_V) {
			Transferable trans = CLIP.getContents(null);
			if (trans != null && trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String text;
				try {
					text = (String) trans.getTransferData(DataFlavor.stringFlavor);
				} catch (UnsupportedFlavorException | IOException e) {
					throw new RuntimeException("Error in pasting text.Info:" + e);
				}
				if (selpostionstart != selpostionend) {
					pa = pa.substring(0, selpostionstart) + text + pa.substring(selpostionend);
					postion = selpostionstart + text.length();
					selpostionstart = selpostionend = 0;
				} else {
					pa = pa.substring(0, postion) + text + pa.substring(postion);
					postion = postion + text.length();
				}
			}
			if (postion > startpaint) {
				String todeal = pa.substring(0, postion);
				StringBuilder bi = new StringBuilder(todeal);
				startpaint = postion
						- CommonRender.subTextWidth(bi.reverse().toString(), size, range.x1 - range.x0).length();
			}
			return;
		}
		// delete
		if (key == GLFW.GLFW_KEY_BACKSPACE || key == GLFW.GLFW_KEY_DELETE) {
			if (selpostionstart != selpostionend) {
				pa = pa.substring(0, selpostionstart) + pa.substring(selpostionend);
				postion = selpostionstart;
				selpostionstart = selpostionend = 0;
			} else {
				if (postion != 0) {
					pa = pa.substring(0, postion - 1) + pa.substring(postion);
					postion = postion - 1;
				}
			}
			StringBuilder bi = new StringBuilder(pa);
			int last_p = CommonRender.subTextWidth(bi.reverse().toString(), size, range.x1 - range.x0).length();
			if (postion >= pa.length() - last_p) {
				startpaint = pa.length() - last_p;
			}
			if (postion < startpaint) {
				String sub = pa.substring(postion);
				int lim = CommonRender.subTextWidth(sub, size, range.x1 - range.x0).length();
				if (lim == sub.length()) {
					startpaint = pa.length() - last_p;
				} else {
					startpaint = postion;
				}
			}
			return;
		}
		if (key == GLFW.GLFW_KEY_ENTER) {
			if (fire_enter != null)
				fire_enter.accept(pa);
			return;
		}

	}

	@Override
	public void onCharInput(int codepoint) {
		// Other Chars
		String get = new String(Character.toChars(codepoint));
		if (selpostionstart != selpostionend) {
			pa = pa.substring(0, selpostionstart) + get + pa.substring(selpostionend);
			postion = selpostionstart + get.length();
			selpostionstart = selpostionend = 0;
		} else {
			pa = pa.substring(0, postion) + get + pa.substring(postion);
			postion = postion + get.length();
		}
		if (postion > startpaint) {
			String todeal = pa.substring(0, postion);
			StringBuilder bi = new StringBuilder(todeal);
			startpaint = postion
					- CommonRender.subTextWidth(bi.reverse().toString(), size, range.x1 - range.x0).length();
		}
	}

	@Override
	public void render() {
		super.render();
		long now_t = ChemistryLab.getTime();
		boolean pt_pos = now_t % 1000 < 500;
		float honzsize = range.x1 - range.x0;
		float vertsize = range.y1 - range.y0;
		float hei = CommonRender.winToOthHeight(size);
		float nexty = range.y0 + vertsize / 2 - hei / 2;
		float all_length = CommonRender.calcTextWidth(pa, size);
		glLineWidth(1);
		if (all_length < honzsize) {
			CommonRender.drawFont(pa, range.x0, nexty, size, color);
			float pos = CommonRender.calcTextWidth(pa.substring(0, postion), size) + range.x0;
			if (parent.isFocus(this) && pos <= range.x1 && pt_pos) {
				glBegin(GL_LINE_STRIP);
				glVertex2f(pos, range.y0);
				glVertex2f(pos, range.y1);
				glEnd();
			}
			if (selpostionstart == selpostionend)
				return;
			float sl_start = CommonRender.calcTextWidth(pa.substring(0, selpostionstart), size) + range.x0;
			float sl_end = CommonRender.calcTextWidth(pa.substring(0, selpostionend), size) + range.x0;
			selcolor.bind();
			glBegin(GL_QUADS);
			glVertex2f(sl_start, range.y0);
			glVertex2f(sl_end, range.y0);
			glVertex2f(sl_end, range.y1);
			glVertex2f(sl_start, range.y1);
			glEnd();
		} else {
			String topaint = CommonRender.subTextWidth(pa.substring(startpaint), size, honzsize);
			float tx = CommonRender.drawFont(topaint, range.x0, nexty, size, color);
			float pos = CommonRender.calcTextWidth(pa.substring(startpaint, postion), size) + range.x0;
			if (parent.isFocus(this) && pos <= range.x1 && pt_pos) {
				glBegin(GL_LINE_STRIP);
				glVertex2f(pos, range.y0);
				glVertex2f(pos, range.y1);
				glEnd();
			}
			if (selpostionstart == selpostionend)
				return;
			float sl_start = (startpaint < selpostionstart
					? CommonRender.calcTextWidth(pa.substring(startpaint, selpostionstart), size)
					: 0) + range.x0;
			float sl_end = (selpostionend - startpaint < topaint.length()
					? CommonRender.calcTextWidth(pa.substring(startpaint, selpostionend), size)
					: tx - range.x0) + range.x0;
			selcolor.bind();
			glBegin(GL_QUADS);
			glVertex2f(sl_start, range.y0);
			glVertex2f(sl_end, range.y0);
			glVertex2f(sl_end, range.y1);
			glVertex2f(sl_start, range.y1);
			glEnd();
		}
	}

}
