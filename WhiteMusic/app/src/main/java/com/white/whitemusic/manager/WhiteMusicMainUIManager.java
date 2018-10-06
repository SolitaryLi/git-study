/**
 * Copyright (c) www.longdw.com
 */
package com.white.whitemusic.manager;

import android.view.View;

public abstract class WhiteMusicMainUIManager {
	
	protected abstract void setBgByPath(String path);
	public abstract View getView();
	public abstract View getView(int from);
	public abstract View getView(int from, Object obj);
}
