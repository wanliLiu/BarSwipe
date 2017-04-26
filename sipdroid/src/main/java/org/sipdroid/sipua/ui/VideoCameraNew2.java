package org.sipdroid.sipua.ui;

/*
 * Copyright (C) 2010 The Sipdroid Open Source Project
 * Copyright (C) 2007 The Android Open Source Project
 * 
 * This file is part of Sipdroid (http://www.sipdroid.org)
 * 
 * Sipdroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.os.Build;

import java.io.IOException;

@TargetApi(Build.VERSION_CODES.FROYO) 
public class VideoCameraNew2 {
	static void reconnect(Camera c) {
		try {
			c.reconnect();
		} catch (IOException e) {
			if (!Sipdroid.release) e.printStackTrace();
		}
	}
}
