/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.refactoring.typeconstraints.types;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ITypeBinding;

import org.eclipse.jdt.internal.corext.Assert;

public final class GenericType extends HierarchyType {
	
	private TType[] fTypeParameters;
	
	protected GenericType(TypeEnvironment environment) {
		super(environment);
	}

	protected void initialize(ITypeBinding binding, IType javaElementType) {
		Assert.isTrue(binding.isGenericType());
		super.initialize(binding, javaElementType);
		TypeEnvironment environment= getEnvironment();
		ITypeBinding[] typeParameters= binding.getTypeParameters();
		fTypeParameters= new TType[typeParameters.length];
		for (int i= 0; i < typeParameters.length; i++) {
			fTypeParameters[i]= environment.create(typeParameters[i]);
		}
	}
	
	public int getKind() {
		return GENERIC_TYPE;
	}
	
	public TType[] getTypeParameters() {
		return (TType[]) fTypeParameters.clone();
	}
	
	public boolean doEquals(TType type) {
		return getJavaElementType().equals(((GenericType)type).getJavaElementType());
	}
	
	public int hashCode() {
		return getJavaElementType().hashCode();
	}
	
	protected boolean doCanAssignTo(TType type) {
		return false;
	}
	
	protected boolean isTypeEquivalentTo(TType other) {
		int otherElementType= other.getKind();
		if (otherElementType == RAW_TYPE || otherElementType == PARAMETERIZED_TYPE)
			return getErasure().isTypeEquivalentTo(other.getErasure());
		return super.isTypeEquivalentTo(other);
	}
	
	public String getName() {
		return getJavaElementType().getElementName();
	}
	
	protected String getPlainPrettySignature() {
		StringBuffer result= new StringBuffer(getJavaElementType().getFullyQualifiedName('.'));
		result.append("<"); //$NON-NLS-1$
		result.append(fTypeParameters[0].getPrettySignature());
		for (int i= 1; i < fTypeParameters.length; i++) {
			result.append(", "); //$NON-NLS-1$
			result.append(fTypeParameters[i].getPrettySignature());
		}
		result.append(">"); //$NON-NLS-1$
		return result.toString();
	}
}
