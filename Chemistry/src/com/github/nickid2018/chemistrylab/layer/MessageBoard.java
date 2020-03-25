package com.github.nickid2018.chemistrylab.layer;

import java.util.*;
import org.lwjgl.glfw.*;
import com.github.nickid2018.chemistrylab.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.render.*;
import com.github.nickid2018.chemistrylab.window.*;

import java.util.concurrent.*;

public class MessageBoard extends Layer {

	public static final MessageBoard INSTANCE = new MessageBoard();

	public static final ArrayList<Message> message_all = new ArrayList<>();

	private ArrayList<Message> message_list = new ArrayList<>();
	private ArrayList<Message> removes = new ArrayList<>();
	private Queue<Message> toAdd = new LinkedBlockingQueue<>();
//	private FastQuad quad = new FastQuad(0, 640, 360, 640, new Color(100, 100, 100, 75), true);
	private int start = 0;

	private Message message;

	private MessageBoard() {
		super(0, 640, 800, 640);
	}

	public void addMessage(Message m) {
		toAdd.offer(m);
		message_all.add(m.clone().setSurviveTime(Long.MAX_VALUE));
		while (message_list.size() > 30)
			message_list.remove(0);
		start = 0;
	}

	@Override
	public void render() {
		while (!toAdd.isEmpty()) {
			message_list.add(toAdd.poll());
		}
		while (message_list.size() > 30)
			message_list.remove(0);
		if (message_list.isEmpty())
			return;
//		quad.render();
		float nowY = range.y1 - 16;
		for (int i = message_list.size() - 1; i >= 0; i--) {
			Message m = message_list.get(i);
			if (!m.isValid()) {
				removes.add(m);
				continue;
			}
			m.render(nowY);
			nowY -= 16;
		}
		message_list.removeAll(removes);
		removes.clear();
		range.x1 = 800;
		range.y1 = MainWindow.nowHeight - 80;
		range.y0 = range.y1 - message_list.size() * 16;
		// Update Vertex
//		quad.updateVertex(FastQuad.POSTION_LEFT_UP, quad.getVertex(FastQuad.POSTION_LEFT_UP).setXYZ(-1,
//				CommonRender.toGLY(range.y1 - message_list.size() * 16), 0));
//		quad.updateVertex(FastQuad.POSTION_RIGHT_UP, quad.getVertex(FastQuad.POSTION_RIGHT_UP)
//				.setXYZ(CommonRender.toGLX(range.x1), CommonRender.toGLY(range.y1 - message_list.size() * 16), 0));
//		quad.updateVertex(FastQuad.POSTION_RIGHT_DOWN, quad.getVertex(FastQuad.POSTION_RIGHT_DOWN)
//				.setXYZ(CommonRender.toGLX(range.x1), CommonRender.toGLY(range.y1), 0));
//		quad.updateVertex(FastQuad.POSTION_LEFT_DOWN,
//				quad.getVertex(FastQuad.POSTION_LEFT_DOWN).setXYZ(-1, CommonRender.toGLY(range.y1), 0));
	}

	@Override
	public void debugRender() {
		while (!toAdd.isEmpty()) {
			message_list.add(toAdd.poll());
		}
//		quad.render();
		if (message_all.isEmpty())
			return;
		float nowY = range.y1 - 16;
		for (int i = message_all.size() - 1 - start; i >= Math.max(message_all.size() - 30 - start, 0); i--) {
			Message m = message_all.get(i);
			m.render(nowY);
			nowY -= 16;
		}
		message_list.removeAll(removes);
		removes.clear();
		range.x1 = 800;
		range.y1 = MainWindow.nowHeight - 80;
		range.y0 = range.y1 - Math.min(message_all.size(), 30) * 16;
		// Update Vertex
//		quad.updateVertex(FastQuad.POSTION_LEFT_UP, quad.getVertex(FastQuad.POSTION_LEFT_UP).setXYZ(-1,
//				CommonRender.toGLY(range.y1 - Math.min(message_all.size(), 30) * 16), 0));
//		quad.updateVertex(FastQuad.POSTION_RIGHT_UP, quad.getVertex(FastQuad.POSTION_RIGHT_UP).setXYZ(
//				CommonRender.toGLX(range.x1), CommonRender.toGLY(range.y1 - Math.min(message_all.size(), 30) * 16), 0));
//		quad.updateVertex(FastQuad.POSTION_RIGHT_DOWN, quad.getVertex(FastQuad.POSTION_RIGHT_DOWN)
//				.setXYZ(CommonRender.toGLX(range.x1), CommonRender.toGLY(range.y1), 0));
//		quad.updateVertex(FastQuad.POSTION_LEFT_DOWN,
//				quad.getVertex(FastQuad.POSTION_LEFT_DOWN).setXYZ(-1, CommonRender.toGLY(range.y1), 0));
	}

	@Override
	public void onContainerResized() {
		doDefaultResize(this);
		range.x1 = 800;
		range.y1 = MainWindow.nowHeight - 80;
		if (HotKeys.f3) {
			range.y0 = range.y1 - Math.min(message_all.size(), 30) * 16;
			// Update Vertex
//			quad.updateVertex(FastQuad.POSTION_LEFT_UP, quad.getVertex(FastQuad.POSTION_LEFT_UP).setXYZ(-1,
//					CommonRender.toGLY(range.y1 - Math.min(message_all.size(), 30) * 16), 0));
//			quad.updateVertex(FastQuad.POSTION_RIGHT_UP,
//					quad.getVertex(FastQuad.POSTION_RIGHT_UP).setXYZ(CommonRender.toGLX(range.x1),
//							CommonRender.toGLY(range.y1 - Math.min(message_all.size(), 30) * 16), 0));
//			quad.updateVertex(FastQuad.POSTION_RIGHT_DOWN, quad.getVertex(FastQuad.POSTION_RIGHT_DOWN)
//					.setXYZ(CommonRender.toGLX(range.x1), CommonRender.toGLY(range.y1), 0));
//			quad.updateVertex(FastQuad.POSTION_LEFT_DOWN,
//					quad.getVertex(FastQuad.POSTION_LEFT_DOWN).setXYZ(-1, CommonRender.toGLY(range.y1), 0));
		} else {
			range.y0 = range.y1 - message_list.size() * 16;
			// Update Vertex
//			quad.updateVertex(FastQuad.POSTION_LEFT_UP, quad.getVertex(FastQuad.POSTION_LEFT_UP).setXYZ(-1,
//					CommonRender.toGLY(range.y1 - message_list.size() * 16), 0));
//			quad.updateVertex(FastQuad.POSTION_RIGHT_UP, quad.getVertex(FastQuad.POSTION_RIGHT_UP)
//					.setXYZ(CommonRender.toGLX(range.x1), CommonRender.toGLY(range.y1 - message_list.size() * 16), 0));
//			quad.updateVertex(FastQuad.POSTION_RIGHT_DOWN, quad.getVertex(FastQuad.POSTION_RIGHT_DOWN)
//					.setXYZ(CommonRender.toGLX(range.x1), CommonRender.toGLY(range.y1), 0));
//			quad.updateVertex(FastQuad.POSTION_LEFT_DOWN,
//					quad.getVertex(FastQuad.POSTION_LEFT_DOWN).setXYZ(-1, CommonRender.toGLY(range.y1), 0));
		}
	}

	@Override
	public void onMouseEvent(int button, int action, int mods) {
		if (action != GLFW.GLFW_PRESS)
			return;
		double y = Mouse.getY();
		int rep = MathHelper.floor((range.y1 - y) / 16);
		try {
			if (HotKeys.f3)
				message_all.get(message_all.size() - rep - 1 - start).onMouseEvent(button, action, mods);
			else
				message_list.get(message_list.size() - rep - 1).onMouseEvent(button, action, mods);
		} catch (Exception e) {
		}
	}

	@Override
	public void onScroll(double xoffset, double yoffset) {
		if (message_all.size() > 30) {
			start = (int) Math.max(0, Math.min(message_all.size() - 30, start + yoffset));
		}
	}

	@Override
	public void onCursorPositionChanged(double xpos, double ypos) {
		int rep = MathHelper.floor((range.y1 - ypos) / 16);
		try {
			Message now;
			if (HotKeys.f3)
				now = message_all.get(message_all.size() - rep - 1 - start);
			else
				now = message_list.get(message_list.size() - rep - 1);
			if (message != now) {
				if (message != null)
					message.onCursorOut();
				message = now;
				message.onCursorIn();
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onCursorOut() {
		if (message != null)
			message.onCursorOut();
		message = null;
	}
}
