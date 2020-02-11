package com.chemistrylab.layer;

import java.util.*;
import org.lwjgl.input.*;
import com.chemistrylab.*;
import org.newdawn.slick.*;
import java.util.concurrent.*;
import com.chemistrylab.util.*;
import com.chemistrylab.init.*;
import com.chemistrylab.render.*;

public class MessageBoard extends Layer {

	public static final MessageBoard INSTANCE = new MessageBoard();

	public static final ArrayList<Message> message_all = new ArrayList<>();

	private ArrayList<Message> message_list = new ArrayList<>();
	private ArrayList<Message> removes = new ArrayList<>();
	private Queue<Message> toAdd = new LinkedBlockingQueue<>();
	private FastQuad quad = new FastQuad(0, 640, 360, 640, new Color(150, 150, 150, 75), true);

	private MessageBoard() {
		super(0, 640, 800, 640);
	}

	public void addMessage(Message m) {
		toAdd.offer(m);
		message_all.add(m.clone().setSurviveTime(Long.MAX_VALUE));
		while (message_list.size() > 30)
			message_list.remove(0);
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
		quad.render();
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
		range.y0 = range.y1 - message_list.size() * 16;
		quad.updateVertex(FastQuad.POSTION_LEFT_UP, quad.getVertex(FastQuad.POSTION_LEFT_UP).setXYZ(-1,
				CommonRender.toGLY(range.y1 - message_list.size() * 16), 0));
		quad.updateVertex(FastQuad.POSTION_RIGHT_UP, quad.getVertex(FastQuad.POSTION_RIGHT_UP)
				.setXYZ(CommonRender.toGLX(range.x1), CommonRender.toGLY(range.y1 - message_list.size() * 16), 0));
		quad.updateVertex(FastQuad.POSTION_RIGHT_DOWN, quad.getVertex(FastQuad.POSTION_RIGHT_DOWN)
				.setXYZ(CommonRender.toGLX(range.x1), CommonRender.toGLY(range.y1), 0));
	}

	@Override
	public void debugRender() {
		render();
	}

	@Override
	public void onContainerResized() {
		doDefaultResize(this);
		range.y0 = range.y1 - message_list.size() * 16;
		quad.updateVertex(FastQuad.POSTION_LEFT_UP, quad.getVertex(FastQuad.POSTION_LEFT_UP).setXYZ(-1,
				CommonRender.toGLY(range.y1 - message_list.size() * 16), 0));
		quad.updateVertex(FastQuad.POSTION_RIGHT_UP, quad.getVertex(FastQuad.POSTION_RIGHT_UP)
				.setXYZ(CommonRender.toGLX(range.x1), CommonRender.toGLY(range.y1 - message_list.size() * 16), 0));
		quad.updateVertex(FastQuad.POSTION_RIGHT_DOWN, quad.getVertex(FastQuad.POSTION_RIGHT_DOWN)
				.setXYZ(CommonRender.toGLX(range.x1), CommonRender.toGLY(range.y1), 0));
	}

	@Override
	public void onMouseEvent() {
		int y = Mouse.getY();
		int rep = MathHelper.floor((-ChemistryLab.nowHeight + y + range.y1 + 16) / 16);
		message_list.get(message_list.size() - rep).onMouseEvent();
	}

}
