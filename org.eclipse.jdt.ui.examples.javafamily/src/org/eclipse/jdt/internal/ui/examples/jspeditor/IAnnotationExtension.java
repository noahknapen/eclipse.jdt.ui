/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jdt.internal.ui.examples.jspeditor;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * Interface of annotations representing markers
 * and problems.
 * 
 * @see org.eclipse.core.resources.IMarker
 * @see org.eclipse.jdt.core.compiler.IProblem
 * @since 3.0
 */
public interface IAnnotationExtension {

	/**
	 * Returns the type of the given annotation.
	 * 
	 * @return the type of the given annotation or <code>null</code> if it has none.
	 */
	Object getType();

	/**
	 * Returns whether the given annotation is temporary rather than persistent.
	 * 
	 * @return <code>true</code> if the annotation is temporary,
	 * 	<code>false</code> otherwise
	 */
	boolean isTemporary();

	/**
	 * Returns the message of this annotation.
	 * 
	 * @return the message of this annotation
	 */
	String getMessage();

	/**
	 * Returns the id of this annotation.
	 * 
	 * @return the id for this annotation or <code>-1</code> if no id is assigned
	 */
	int getId();

	/**
	 * Returns an image for this annotation.
	 * 
	 * @param display the display for which the image is requested
	 * @return the image for this annotation
	 */
	Image getImage(Display display);
}
