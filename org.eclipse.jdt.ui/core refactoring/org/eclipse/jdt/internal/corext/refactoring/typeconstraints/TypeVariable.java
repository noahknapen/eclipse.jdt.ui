/*******************************************************************************
 * Copyright (c) 2000, 2002 International Business Machines Corp. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v0.5 
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v05.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.jdt.internal.corext.refactoring.typeconstraints;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.Type;

import org.eclipse.jdt.internal.corext.Assert;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;

public final class TypeVariable extends ConstraintVariable {

	private final String fSource;
	private final CompilationUnitRange fTypeRange;
	
	public TypeVariable(Type type){
		super(type.resolveBinding());
		fSource= type.toString();
		ICompilationUnit cu= ASTCreator.getCu(type);
		Assert.isNotNull(cu);
		fTypeRange= new CompilationUnitRange(cu, ASTNodes.getElementType(type));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return fSource;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (! super.equals(obj))
			return false;
		if (! (obj instanceof TypeVariable))
			return false;
		TypeVariable other= (TypeVariable)obj;
		//cannot compare bindings here
		return fTypeRange.equals(other.fTypeRange);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		if (super.getBinding() == null)
			return super.hashCode();
		return (29 * super.hashCode()) ^ fTypeRange.hashCode();
	}

	public CompilationUnitRange getCompilationUnitRange() {
		return fTypeRange;
	}
}