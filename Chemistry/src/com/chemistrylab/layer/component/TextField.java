package com.chemistrylab.layer.component;

import java.io.*;
import java.awt.Toolkit;
import org.lwjgl.input.*;
import java.util.function.*;
import com.chemistrylab.*;
import org.newdawn.slick.*;
import java.awt.datatransfer.*;
import com.chemistrylab.layer.*;
import com.chemistrylab.render.*;

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

	public TextField(int x0, int y0, int x1, int y1, Layer l, int size) {
		this(x0, y0, x1, y1, l, size, "");
	}

	public TextField(int x0, int y0, int x1, int y1, Layer l, int size, String s) {
		super(x0, y0, x1, y1, l);
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
	private long last_focus = -1;

	@Override
	public synchronized void onMouseEvent() {
		if (!Mouse.isButtonDown(0))
			return;
		if (ChemistryLab.getTime() - last_focus > 20) {
			focus_on = false;
			started = false;
			start_to_sel = selpostionstart = selpostionend = 0;
		}
		last_focus = ChemistryLab.getTime();
		float len = CommonRender.winToOthWidth(Mouse.getX());
		String s = CommonRender.subTextWidth(pa.substring(startpaint), size, len - range.x0);
		postion = startpaint + s.length();
		if (focus_on) {
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
				start_to_sel = selpostionstart = selpostionend = postion;
			}
		}
		focus_on = true;
	}

	@Override
	public void onKeyActive() {
		// shift
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			shift_on = true;
		} else {
			shift_on = false;
		}
		// Ctrl+A select all
		if ((Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
				&& Keyboard.isKeyDown(Keyboard.KEY_A)) {
			selpostionstart = 0;
			selpostionend = pa.length();
			return;
		}
		int tmp = postion;
		// left
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
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
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
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
		if ((Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
				&& Keyboard.isKeyDown(Keyboard.KEY_C)) {
			if (selpostionstart != selpostionend) {
				Transferable trans = new StringSelection(pa.substring(selpostionstart, selpostionend));
				CLIP.setContents(trans, null);
			}
			return;
		}
		// Ctrl+X cut
		if ((Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
				&& Keyboard.isKeyDown(Keyboard.KEY_X)) {
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
		// Ctrl+P paste
		if ((Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
				&& Keyboard.isKeyDown(Keyboard.KEY_V)) {
			Transferable trans = CLIP.getContents(null);
			if (trans != null && trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String text;
				try {
					text = (String) trans.getTransferData(DataFlavor.stringFlavor);
				} catch (UnsupportedFlavorException | IOException e) {
					throw new RuntimeException("Error in pasting text.");
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
		if (Keyboard.isKeyDown(Keyboard.KEY_DELETE) || Keyboard.isKeyDown(Keyboard.KEY_BACK)) {
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
		if(Keyboard.isKeyDown(Keyboard.KEY_RETURN)){
			if(fire_enter != null)
				fire_enter.accept(pa);
			return;
		}
		// Other Chars
		char get = Keyboard.getEventCharacter();
		if (Character.isISOControl(get))
			return;
		if (selpostionstart != selpostionend) {
			pa = pa.substring(0, selpostionstart) + get + pa.substring(selpostionend);
			postion = selpostionstart + 1;
			selpostionstart = selpostionend = 0;
		} else {
			pa = pa.substring(0, postion) + get + pa.substring(postion);
			postion = postion + 1;
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
			if (pos <= range.x1 && pt_pos) {
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
			if (pos <= range.x1 && pt_pos) {
				glBegin(GL_LINE_STRIP);
					glVertex2f(pos, range.y0);
					glVertex2f(pos, range.y1);
				glEnd();
			}
			if (selpostionstart == selpostionend)
				return;
			float sl_start = (startpaint < selpostionstart
					? CommonRender.calcTextWidth(pa.substring(startpaint, selpostionstart), size) : 0) + range.x0;
			float sl_end = (selpostionend - startpaint < topaint.length()
					? CommonRender.calcTextWidth(pa.substring(startpaint, selpostionend), size) : tx - range.x0)
					+ range.x0;
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
