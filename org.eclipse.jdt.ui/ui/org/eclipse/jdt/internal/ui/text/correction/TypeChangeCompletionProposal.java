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

package org.eclipse.jdt.internal.ui.text.correction;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jdt.core.ICompilationUnit;

import org.eclipse.jdt.core.dom.*;

import org.eclipse.jdt.internal.corext.dom.ASTNodeFactory;
import org.eclipse.jdt.internal.corext.dom.ASTRewrite;
import org.eclipse.jdt.internal.ui.JavaPluginImages;

public class TypeChangeCompletionProposal extends LinkedCorrectionProposal {

	private IBinding fBinding;
	private CompilationUnit fAstRoot;
	private ITypeBinding fNewType;
	private boolean fOfferSuperTypeProposals;
	
	public TypeChangeCompletionProposal(ICompilationUnit targetCU, IBinding binding, CompilationUnit astRoot, ITypeBinding newType, boolean offerSuperTypeProposals, int relevance) {
		super("", targetCU, null, relevance, JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CHANGE)); //$NON-NLS-1$
		fBinding= binding;
		fAstRoot= astRoot;
		fNewType= newType;
		fOfferSuperTypeProposals= offerSuperTypeProposals;
		
		
		if (binding.getKind() == IBinding.VARIABLE) {
			String[] args= { binding.getName(), newType.getName() };
			if (((IVariableBinding) binding).isField()) {
				setDisplayName(CorrectionMessages.getFormattedString("TypeChangeCompletionProposal.field.name", args)); //$NON-NLS-1$
			} else if (astRoot.findDeclaringNode(binding) instanceof SingleVariableDeclaration) {
				setDisplayName(CorrectionMessages.getFormattedString("TypeChangeCompletionProposal.param.name", args)); //$NON-NLS-1$
			} else {
				setDisplayName(CorrectionMessages.getFormattedString("TypeChangeCompletionProposal.variable.name", args)); //$NON-NLS-1$
			}
		} else {
			ITypeBinding declClass= ((IMethodBinding) binding).getDeclaringClass();
			String[] args= { declClass.getName(), binding.getName(), newType.getName() };
			setDisplayName(CorrectionMessages.getFormattedString("TypeChangeCompletionProposal.method.name", args)); //$NON-NLS-1$
		}
	}
	
	protected ASTRewrite getRewrite() throws CoreException {
		ASTNode boundNode= fAstRoot.findDeclaringNode(fBinding);
		ASTNode declNode= null;
				
		if (boundNode != null) {
			declNode= boundNode; // is same CU
		} else {
			ASTParser astParser= ASTParser.newParser(AST.LEVEL_2_0);
			astParser.setSource(getCompilationUnit());
			astParser.setResolveBindings(true);
			CompilationUnit newRoot= (CompilationUnit) astParser.createAST(null);
			declNode= newRoot.findDeclaringNode(fBinding.getKey());
		}
		if (declNode != null) {
			AST ast= declNode.getAST();
			ASTRewrite rewrite= new ASTRewrite(ast);
			
			String typeString= getImportRewrite().addImport(fNewType);
			Type type= ASTNodeFactory.newType(ast, typeString);
			
			if (declNode instanceof MethodDeclaration) {
				MethodDeclaration methodDecl= (MethodDeclaration) declNode;
				rewrite.set(methodDecl, MethodDeclaration.RETURN_TYPE_PROPERTY, type, null);
			} else if (declNode instanceof VariableDeclarationFragment) {
				ASTNode parent= declNode.getParent();
				if (parent instanceof FieldDeclaration) {
					FieldDeclaration fieldDecl= (FieldDeclaration) parent;
					rewrite.set(fieldDecl, FieldDeclaration.TYPE_PROPERTY, type, null);
				} else if (parent instanceof VariableDeclarationStatement) {
					VariableDeclarationStatement varDecl= (VariableDeclarationStatement) parent;
					
					rewrite.set(varDecl, VariableDeclarationStatement.TYPE_PROPERTY, type, null);
				} else if (parent instanceof VariableDeclarationExpression) {
					VariableDeclarationExpression varDecl= (VariableDeclarationExpression) parent;
					
					rewrite.set(varDecl, VariableDeclarationExpression.TYPE_PROPERTY, type, null);
				}
			} else if (declNode instanceof SingleVariableDeclaration) {
				SingleVariableDeclaration variableDeclaration= (SingleVariableDeclaration) declNode;
				rewrite.set(variableDeclaration, SingleVariableDeclaration.TYPE_PROPERTY, type, null);
			}
			final String KEY_TYPE= "type"; //$NON-NLS-1$
			addLinkedPosition(rewrite.track(type), true, KEY_TYPE);
			if (fOfferSuperTypeProposals) {
				ITypeBinding[] typeProposals= ASTResolving.getRelaxingTypes(ast, fNewType);
				for (int i= 0; i < typeProposals.length; i++) {
					addLinkedPositionProposal(KEY_TYPE, typeProposals[i]);
				}
			}
			return rewrite;
		}
		return null;
	}
	
	
}
