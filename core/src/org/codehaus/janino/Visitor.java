
/*
 * Janino - An embedded Java[TM] compiler
 *
 * Copyright (c) 2001-2010 Arno Unkrig. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *       following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *       following disclaimer in the documentation and/or other materials provided with the distribution.
 *    3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote
 *       products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.codehaus.janino;

import org.codehaus.commons.nullanalysis.Nullable;
import org.codehaus.janino.Java.Package;
import org.codehaus.janino.Java.*;

/**
 * The basis for the "visitor" pattern as described in "Gamma, Helm, Johnson, Vlissides: Design Patterns".
 */
public final
class Visitor {

    private Visitor() {}

    /**
     * The visitor for the different kinds of {@link AbstractCompilationUnit}s.
     *
     * @param <R>  The type of the object returned by the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface AbstractCompilationUnitVisitor<R, EX extends Throwable> {

        /**
         * Invoked by {@link CompilationUnit#accept(AbstractCompilationUnitVisitor)}
         */
        @Nullable R
        visitCompilationUnit(CompilationUnit cu) throws EX;

        /**
         * Invoked by {@link ModularCompilationUnit#accept(AbstractCompilationUnitVisitor)}
         */
        @Nullable R
        visitModularCompilationUnit(ModularCompilationUnit mcu) throws EX;
    }

    /**
     * The visitor for the different kinds of {@link Java.ModuleDirective}s.
     *
     * @param <R>  The type of the object returned by the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface ModuleDirectiveVisitor<R, EX extends Throwable> {
        @Nullable R visitRequiresModuleDirective(RequiresModuleDirective rmd) throws EX;
        @Nullable R visitExportsModuleDirective(ExportsModuleDirective emd)   throws EX;
        @Nullable R visitOpensModuleDirective(OpensModuleDirective omd)       throws EX;
        @Nullable R visitUsesModuleDirective(UsesModuleDirective umd)         throws EX;
        @Nullable R visitProvidesModuleDirective(ProvidesModuleDirective pmd) throws EX;
    }

    /**
     * The visitor for all kinds of {@link AbstractCompilationUnit.ImportDeclaration}s.
     *
     * @param <R>  The type of the object returned by the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface ImportVisitor<R, EX extends Throwable> {

        /**
         * Invoked by {@link AbstractCompilationUnit.SingleTypeImportDeclaration#accept(ImportVisitor)}
         */
        @Nullable R
        visitSingleTypeImportDeclaration(AbstractCompilationUnit.SingleTypeImportDeclaration stid) throws EX;

        /**
         * Invoked by {@link AbstractCompilationUnit.TypeImportOnDemandDeclaration#accept(ImportVisitor)}
         */
        @Nullable R
        visitTypeImportOnDemandDeclaration(AbstractCompilationUnit.TypeImportOnDemandDeclaration tiodd) throws EX;

        /**
         * Invoked by {@link AbstractCompilationUnit.SingleStaticImportDeclaration#accept(ImportVisitor)}
         */
        @Nullable R
        visitSingleStaticImportDeclaration(AbstractCompilationUnit.SingleStaticImportDeclaration ssid) throws EX;

        /**
         * Invoked by {@link AbstractCompilationUnit.StaticImportOnDemandDeclaration#accept(ImportVisitor)}
         */
        @Nullable R
        visitStaticImportOnDemandDeclaration(AbstractCompilationUnit.StaticImportOnDemandDeclaration siodd) throws EX;
    }

    /**
     * The visitor for all kinds of {@link Java.TypeDeclaration}s.
     *
     * @param <R>  The type of the object returned by the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface TypeDeclarationVisitor<R, EX extends Throwable> {

        /**
         * Invoked by {@link AnonymousClassDeclaration#accept(TypeDeclarationVisitor)}
         */
        @Nullable R visitAnonymousClassDeclaration(AnonymousClassDeclaration acd) throws EX;

        /**
         * Invoked by {@link LocalClassDeclaration#accept(TypeDeclarationVisitor)}
         */
        @Nullable R visitLocalClassDeclaration(LocalClassDeclaration lcd) throws EX;

        /**
         * Invoked by {@link PackageMemberClassDeclaration#accept(TypeDeclarationVisitor)}
         */
        @Nullable R visitPackageMemberClassDeclaration(PackageMemberClassDeclaration pmcd) throws EX;

        /**
         * Invoked by {@link MemberInterfaceDeclaration#accept(TypeDeclarationVisitor)}
         */
        @Nullable R visitMemberInterfaceDeclaration(MemberInterfaceDeclaration mid) throws EX;

        /**
         * Invoked by {@link PackageMemberInterfaceDeclaration#accept(TypeDeclarationVisitor)}
         */
        @Nullable R visitPackageMemberInterfaceDeclaration(PackageMemberInterfaceDeclaration pmid) throws EX;

        /**
         * Invoked by {@link MemberClassDeclaration#accept(TypeDeclarationVisitor)}
         */
        @Nullable R visitMemberClassDeclaration(MemberClassDeclaration mcd) throws EX;

        /**
         * Invoked by {@link EnumConstant#accept(TypeDeclarationVisitor)}
         */
        @Nullable R visitEnumConstant(EnumConstant ec) throws EX;

        /**
         * Invoked by {@link MemberEnumDeclaration#accept(TypeDeclarationVisitor)}
         */
        @Nullable R visitMemberEnumDeclaration(MemberEnumDeclaration med) throws EX;

        /**
         * Invoked by {@link PackageMemberEnumDeclaration#accept(TypeDeclarationVisitor)}
         */
        @Nullable R visitPackageMemberEnumDeclaration(PackageMemberEnumDeclaration pmed) throws EX;

        /**
         * Invoked by {@link MemberAnnotationTypeDeclaration#accept(TypeDeclarationVisitor)}
         */
        @Nullable R visitMemberAnnotationTypeDeclaration(MemberAnnotationTypeDeclaration matd) throws EX;

        /**
         * Invoked by {@link PackageMemberAnnotationTypeDeclaration#accept(TypeDeclarationVisitor)}
         */
        @Nullable R visitPackageMemberAnnotationTypeDeclaration(PackageMemberAnnotationTypeDeclaration pmatd) throws EX;
    }

    /**
     * The visitor for all kinds of {@link FunctionDeclarator}s.
     *
     * @param <R>  The type of the object returned by the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface FunctionDeclaratorVisitor<R, EX extends Throwable> {

        /**
         * Invoked by {@link ConstructorDeclarator#accept(TypeBodyDeclarationVisitor)}
         */
        @Nullable R visitConstructorDeclarator(ConstructorDeclarator cd) throws EX;

        /**
         * Invoked by {@link MethodDeclarator#accept(TypeBodyDeclarationVisitor)}
         */
        @Nullable R visitMethodDeclarator(MethodDeclarator md) throws EX;
    }

    /**
     * The visitor for all kinds of {@link Java.TypeBodyDeclaration}s (declarations that may appear in the body of a
     * type declaration).
     *
     * @param <R>  The type of the object returned by the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface TypeBodyDeclarationVisitor<R, EX extends Throwable> {

        /**
         * Invoked by {@link MemberInterfaceDeclaration#accept(TypeBodyDeclarationVisitor)}
         */
        @Nullable R visitMemberInterfaceDeclaration(MemberInterfaceDeclaration mid) throws EX;

        /**
         * Invoked by {@link MemberClassDeclaration#accept(TypeBodyDeclarationVisitor)}
         */
        @Nullable R visitMemberClassDeclaration(MemberClassDeclaration mcd) throws EX;

        /**
         * Invoked by {@link Initializer#accept(TypeBodyDeclarationVisitor)}
         */
        @Nullable R visitInitializer(Initializer i) throws EX;

        /**
         * Invoked by {@link FieldDeclaration#accept(TypeBodyDeclarationVisitor)}
         */
        @Nullable R visitFieldDeclaration(FieldDeclaration fd) throws EX;

        /**
         * Invoked by {@link MemberEnumDeclaration#accept(TypeBodyDeclarationVisitor)}
         */
        @Nullable R visitMemberEnumDeclaration(MemberEnumDeclaration med) throws EX;

        /**
         * Invoked by {@link FunctionDeclarator#accept(TypeBodyDeclarationVisitor)}.
         */
        @Nullable R visitFunctionDeclarator(FunctionDeclarator fd) throws EX;

        /**
         * Invoked by {@link MemberAnnotationTypeDeclaration#accept(TypeBodyDeclarationVisitor)}.
         */
        @Nullable R visitMemberAnnotationTypeDeclaration(MemberAnnotationTypeDeclaration matd) throws EX;
    }

    /**
     * The visitor for all kinds of {@link Java.BlockStatement}s (statements that may appear with a block).
     *
     * @param <R>  The type of the object returned by the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface BlockStatementVisitor<R, EX extends Throwable> {

        /**
         * Invoked by {@link Initializer#accept(BlockStatementVisitor)}
         */
        @Nullable R visitInitializer(Initializer i) throws EX;

        /**
         * Invoked by {@link FieldDeclaration#accept(BlockStatementVisitor)}
         */
        @Nullable R visitFieldDeclaration(FieldDeclaration fd) throws EX;

        /**
         * Invoked by {@link LabeledStatement#accept(BlockStatementVisitor)}
         */
        @Nullable R visitLabeledStatement(LabeledStatement ls) throws EX;

        /**
         * Invoked by {@link Block#accept(BlockStatementVisitor)}
         */
        @Nullable R visitBlock(Block b) throws EX;

        /**
         * Invoked by {@link ExpressionStatement#accept(BlockStatementVisitor)}
         */
        @Nullable R visitExpressionStatement(ExpressionStatement es) throws EX;

        /**
         * Invoked by {@link IfStatement#accept(BlockStatementVisitor)}
         */
        @Nullable R visitIfStatement(IfStatement is) throws EX;

        /**
         * Invoked by {@link ForStatement#accept(BlockStatementVisitor)}
         */
        @Nullable R visitForStatement(ForStatement fs) throws EX;

        /**
         * Invoked by {@link ForEachStatement#accept(BlockStatementVisitor)}
         */
        @Nullable R visitForEachStatement(ForEachStatement forEachStatement) throws EX;

        /**
         * Invoked by {@link WhileStatement#accept(BlockStatementVisitor)}
         */
        @Nullable R visitWhileStatement(WhileStatement ws) throws EX;

        /**
         * Invoked by {@link TryStatement#accept(BlockStatementVisitor)}
         */
        @Nullable R visitTryStatement(TryStatement ts) throws EX;

        /**
         * Invoked by {@link SwitchStatement#accept(BlockStatementVisitor)}
         */
        @Nullable R visitSwitchStatement(SwitchStatement ss) throws EX;

        /**
         * Invoked by {@link SynchronizedStatement#accept(BlockStatementVisitor)}
         */
        @Nullable R visitSynchronizedStatement(SynchronizedStatement ss) throws EX;

        /**
         * Invoked by {@link DoStatement#accept(BlockStatementVisitor)}
         */
        @Nullable R visitDoStatement(DoStatement ds) throws EX;

        /**
         * Invoked by {@link LocalVariableDeclarationStatement#accept(BlockStatementVisitor)}
         */
        @Nullable R visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement lvds) throws EX;

        /**
         * Invoked by {@link ReturnStatement#accept(BlockStatementVisitor)}
         */
        @Nullable R visitReturnStatement(ReturnStatement rs) throws EX;

        /**
         * Invoked by {@link ThrowStatement#accept(BlockStatementVisitor)}
         */
        @Nullable R visitThrowStatement(ThrowStatement ts) throws EX;

        /**
         * Invoked by {@link BreakStatement#accept(BlockStatementVisitor)}
         */
        @Nullable R visitBreakStatement(BreakStatement bs) throws EX;

        /**
         * Invoked by {@link ContinueStatement#accept(BlockStatementVisitor)}
         */
        @Nullable R visitContinueStatement(ContinueStatement cs) throws EX;

        /**
         * Invoked by {@link AssertStatement#accept(BlockStatementVisitor)}
         */
        @Nullable R visitAssertStatement(AssertStatement as) throws EX;

        /**
         * Invoked by {@link EmptyStatement#accept(BlockStatementVisitor)}
         */
        @Nullable R visitEmptyStatement(EmptyStatement es) throws EX;

        /**
         * Invoked by {@link LocalClassDeclarationStatement#accept(BlockStatementVisitor)}
         */
        @Nullable R visitLocalClassDeclarationStatement(LocalClassDeclarationStatement lcds) throws EX;

        /**
         * Invoked by {@link AlternateConstructorInvocation#accept(BlockStatementVisitor)}
         */
        @Nullable R visitAlternateConstructorInvocation(AlternateConstructorInvocation aci) throws EX;

        /**
         * Invoked by {@link SuperConstructorInvocation#accept(BlockStatementVisitor)}
         */
        @Nullable R visitSuperConstructorInvocation(SuperConstructorInvocation sci) throws EX;
    }

    /**
     * The visitor for all kinds of {@link Java.Atom}s.
     *
     * @param <R>  The type of the object returned by the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface AtomVisitor<R, EX extends Throwable> {

        /**
         * Invoked by {@link Package#accept(AtomVisitor)}.
         */
        @Nullable R visitPackage(Package p) throws EX;

        /**
         * Invoked by {@link Rvalue#accept(AtomVisitor)}.
         */
        @Nullable R visitRvalue(Rvalue rv) throws EX;

        /**
         * Invoked by {@link Type#accept(AtomVisitor)}.
         */
        @Nullable R visitType(Type t) throws EX;

        /**
         * Invoked by {@link ConstructorInvocation#accept(AtomVisitor)}.
         */
        @Nullable R visitConstructorInvocation(ConstructorInvocation ci) throws EX;
    }

    /**
     * The visitor for all kinds of {@link Type}s.
     *
     * @param <R>  The type of the object returned by the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface TypeVisitor<R, EX extends Throwable> {

        /**
         * Invoked by {@link ArrayType#accept(TypeVisitor)}
         */
        @Nullable R visitArrayType(ArrayType at) throws EX;

        /**
         * Invoked by {@link PrimitiveType#accept(TypeVisitor)}
         */
        @Nullable R visitPrimitiveType(PrimitiveType bt) throws EX;

        /**
         * Invoked by {@link ReferenceType#accept(TypeVisitor)}
         */
        @Nullable R visitReferenceType(ReferenceType rt) throws EX;

        /**
         * Invoked by {@link RvalueMemberType#accept(TypeVisitor)}
         */
        @Nullable R visitRvalueMemberType(RvalueMemberType rmt) throws EX;

        /**
         * Invoked by {@link SimpleType#accept(TypeVisitor)}
         */
        @Nullable R visitSimpleType(SimpleType st) throws EX;
    }

    /**
     * The visitor for all kinds of {@link Rvalue}s.
     *
     * @param <R>  The type of the object returned by the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface RvalueVisitor<R, EX extends Throwable> {

        /**
         * Invoked by {@link Lvalue#accept(RvalueVisitor)}
         */
        @Nullable R visitLvalue(Lvalue lv) throws EX;

        /**
         * Invoked by {@link ArrayLength#accept(RvalueVisitor)}
         */
        @Nullable R visitArrayLength(ArrayLength al) throws EX;

        /**
         * Invoked by {@link Assignment#accept(RvalueVisitor)}
         */
        @Nullable R visitAssignment(Assignment a) throws EX;

        /**
         * Invoked by {@link UnaryOperation#accept(RvalueVisitor)}
         */
        @Nullable R visitUnaryOperation(UnaryOperation uo) throws EX;

        /**
         * Invoked by {@link BinaryOperation#accept(RvalueVisitor)}
         */
        @Nullable R visitBinaryOperation(BinaryOperation bo) throws EX;

        /**
         * Invoked by {@link Cast#accept(RvalueVisitor)}
         */
        @Nullable R visitCast(Cast c) throws EX;

        /**
         * Invoked by {@link ClassLiteral#accept(RvalueVisitor)}
         */
        @Nullable R visitClassLiteral(ClassLiteral cl) throws EX;

        /**
         * Invoked by {@link ConditionalExpression#accept(RvalueVisitor)}
         */
        @Nullable R visitConditionalExpression(ConditionalExpression ce) throws EX;

        /**
         * Invoked by {@link Crement#accept(RvalueVisitor)}
         */
        @Nullable R visitCrement(Crement c) throws EX;

        /**
         * Invoked by {@link Instanceof#accept(RvalueVisitor)}
         */
        @Nullable R visitInstanceof(Instanceof io) throws EX;

        /**
         * Invoked by {@link MethodInvocation#accept(RvalueVisitor)}
         */
        @Nullable R visitMethodInvocation(MethodInvocation mi) throws EX;

        /**
         * Invoked by {@link SuperclassMethodInvocation#accept(RvalueVisitor)}
         */
        @Nullable R visitSuperclassMethodInvocation(SuperclassMethodInvocation smi) throws EX;

        /**
         * Invoked by {@link IntegerLiteral#accept(RvalueVisitor)}
         */
        @Nullable R visitIntegerLiteral(IntegerLiteral il) throws EX;

        /**
         * Invoked by {@link FloatingPointLiteral#accept(RvalueVisitor)}
         */
        @Nullable R visitFloatingPointLiteral(FloatingPointLiteral fpl) throws EX;

        /**
         * Invoked by {@link BooleanLiteral#accept(RvalueVisitor)}
         */
        @Nullable R visitBooleanLiteral(BooleanLiteral bl) throws EX;

        /**
         * Invoked by {@link CharacterLiteral#accept(RvalueVisitor)}
         */
        @Nullable R visitCharacterLiteral(CharacterLiteral cl) throws EX;

        /**
         * Invoked by {@link StringLiteral#accept(RvalueVisitor)}
         */
        @Nullable R visitStringLiteral(StringLiteral sl) throws EX;

        /**
         * Invoked by {@link NullLiteral#accept(RvalueVisitor)}
         */
        @Nullable R visitNullLiteral(NullLiteral nl) throws EX;

        /**
         * Invoked by {@link SimpleConstant#accept(RvalueVisitor)}
         */
        @Nullable R visitSimpleConstant(SimpleConstant sl) throws EX;

        /**
         * Invoked by {@link NewAnonymousClassInstance#accept(RvalueVisitor)}
         */
        @Nullable R visitNewAnonymousClassInstance(NewAnonymousClassInstance naci) throws EX;

        /**
         * Invoked by {@link NewArray#accept(RvalueVisitor)}
         */
        @Nullable R visitNewArray(NewArray na) throws EX;

        /**
         * Invoked by {@link NewInitializedArray#accept(RvalueVisitor)}
         */
        @Nullable R visitNewInitializedArray(NewInitializedArray nia) throws EX;

        /**
         * Invoked by {@link NewClassInstance#accept(RvalueVisitor)}
         */
        @Nullable R visitNewClassInstance(NewClassInstance nci) throws EX;

        /**
         * Invoked by {@link ParameterAccess#accept(RvalueVisitor)}
         */
        @Nullable R visitParameterAccess(ParameterAccess pa) throws EX;

        /**
         * Invoked by {@link QualifiedThisReference#accept(RvalueVisitor)}
         */
        @Nullable R visitQualifiedThisReference(QualifiedThisReference qtr) throws EX;

        /**
         * Invoked by {@link ArrayLength#accept(RvalueVisitor)}
         */
        @Nullable R visitThisReference(ThisReference tr) throws EX;

        /**
         * Invoked by {@link LambdaExpression#accept(RvalueVisitor)}
         */
        @Nullable R visitLambdaExpression(LambdaExpression le) throws EX;

        /**
         * Invoked by {@link MethodReference#accept(RvalueVisitor)}
         */
        @Nullable R visitMethodReference(MethodReference mr) throws EX;

        /**
         * Invoked by {@link ClassInstanceCreationReference#accept(RvalueVisitor)}
         */
        @Nullable R visitInstanceCreationReference(ClassInstanceCreationReference cicr) throws EX;

        /**
         * Invoked by {@link ArrayCreationReference#accept(RvalueVisitor)}
         */
        @Nullable R visitArrayCreationReference(ArrayCreationReference acr) throws EX;
    }

    /**
     * The visitor for all kinds of {@link Lvalue}s.
     *
     * @param <R>  The type of the object returned by the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface LvalueVisitor<R, EX extends Throwable> {

        /**
         * Invoked by {@link AmbiguousName#accept(LvalueVisitor)}
         */
        @Nullable R visitAmbiguousName(AmbiguousName an) throws EX;

        /**
         * Invoked by {@link ArrayAccessExpression#accept(LvalueVisitor)}
         */
        @Nullable R visitArrayAccessExpression(ArrayAccessExpression aae) throws EX;

        /**
         * Invoked by {@link FieldAccess#accept(LvalueVisitor)}
         */
        @Nullable R visitFieldAccess(FieldAccess fa) throws EX;

        /**
         * Invoked by {@link FieldAccessExpression#accept(LvalueVisitor)}
         */
        @Nullable R visitFieldAccessExpression(FieldAccessExpression fae) throws EX;

        /**
         * Invoked by {@link SuperclassFieldAccessExpression#accept(LvalueVisitor)}
         */
        @Nullable R visitSuperclassFieldAccessExpression(SuperclassFieldAccessExpression scfae) throws EX;

        /**
         * Invoked by {@link LocalVariableAccess#accept(LvalueVisitor)}
         */
        @Nullable R visitLocalVariableAccess(LocalVariableAccess lva) throws EX;

        /**
         * Invoked by {@link ParenthesizedExpression#accept(LvalueVisitor)}
         */
        @Nullable R visitParenthesizedExpression(ParenthesizedExpression pe) throws EX;
    }

    /**
     * The visitor for all kinds of {@link ConstructorInvocation}s.
     *
     * @param <R>  The type of the object returned by the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface ConstructorInvocationVisitor<R, EX extends Throwable> {

        /**
         * Invoked by {@link MarkerAnnotation#accept(AnnotationVisitor)}
         */
        @Nullable R visitAlternateConstructorInvocation(AlternateConstructorInvocation aci) throws EX;

        /**
         * Invoked by {@link SingleElementAnnotation#accept(AnnotationVisitor)}
         */
        @Nullable R visitSuperConstructorInvocation(SuperConstructorInvocation sci) throws EX;
    }

    /**
     * The visitor for all kinds of {@link Annotation}s.
     *
     * @param <R>  The type of the object returned by the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface AnnotationVisitor<R, EX extends Throwable> {

        /**
         * Invoked by {@link MarkerAnnotation#accept(AnnotationVisitor)}
         */
        @Nullable R visitMarkerAnnotation(MarkerAnnotation ma) throws EX;

        /**
         * Invoked by {@link NormalAnnotation#accept(AnnotationVisitor)}
         */
        @Nullable R visitNormalAnnotation(NormalAnnotation na) throws EX;

        /**
         * Invoked by {@link SingleElementAnnotation#accept(AnnotationVisitor)}
         */
        @Nullable R visitSingleElementAnnotation(SingleElementAnnotation sea) throws EX;
    }

    /**
     * @param <R>  The type of the object returned by the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface ElementValueVisitor<R, EX extends Throwable> {

        /**
         * Invoked by {@link Rvalue#accept(ElementValueVisitor)}
         */
        @Nullable R visitRvalue(Rvalue rv) throws EX;

        /**
         * Invoked by {@link Annotation#accept(ElementValueVisitor)}
         */
        @Nullable R visitAnnotation(Annotation a) throws EX;

        /**
         * Invoked by {@link ElementValueArrayInitializer#accept(ElementValueVisitor)}
         */
        @Nullable R visitElementValueArrayInitializer(ElementValueArrayInitializer evai) throws EX;
    }

    /**
     * The visitor for all kinds of {@link Java.TypeArgument}s.
     *
     * @param <R>  The type of the object returned by the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface TypeArgumentVisitor<R, EX extends Throwable> {

        /**
         * Invoked by {@link Wildcard#accept(TypeArgumentVisitor)}
         */
        @Nullable R visitWildcard(Wildcard w) throws EX;

        /**
         * Invoked by {@link ReferenceType#accept(TypeArgumentVisitor)}
         */
        @Nullable R visitReferenceType(ReferenceType rt) throws EX;

        /**
         * Invoked by {@link ArrayType#accept(TypeArgumentVisitor)}
         */
        @Nullable R visitArrayType(ArrayType arrayType) throws EX;
    }

    /**
     * The visitor for the different kinds of {@link Java.LambdaParameters} styles.
     *
     * @param <R>  The type of the object returned by the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface LambdaParametersVisitor<R, EX extends Throwable> { // SUPPRESS CHECKSTYLE Javadoc:4

        @Nullable R visitIdentifierLambdaParameters(IdentifierLambdaParameters ilp) throws EX;
        @Nullable R visitFormalLambdaParameters(FormalLambdaParameters flp)         throws EX;
        @Nullable R visitInferredLambdaParameters(InferredLambdaParameters ilp)     throws EX;
    }

    /**
     * The visitor for the different kinds of {@link Java.LambdaBody}s.
     *
     * @param <R>  The type of the object returned by the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface LambdaBodyVisitor<R, EX extends Throwable> { // SUPPRESS CHECKSTYLE Javadoc:3

        @Nullable R visitBlockLambdaBody(BlockLambdaBody blockLambdaBody)                throws EX;
        @Nullable R visitExpressionLambdaBody(ExpressionLambdaBody expressionLambdaBody) throws EX;
    }

    /**
     * The visitor for all kinds of {@link TryStatement.Resource}s.
     *
     * @param <R>  The type of the object returned by the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface TryStatementResourceVisitor<R, EX extends Throwable> {

        /**
         * Invoked by {@link
         * TryStatement.LocalVariableDeclaratorResource#accept(TryStatementResourceVisitor)}.
         */
        @Nullable R
        visitLocalVariableDeclaratorResource(TryStatement.LocalVariableDeclaratorResource lvdr) throws EX;

        /**
         * Invoked by {@link TryStatement.VariableAccessResource#accept(TryStatementResourceVisitor)}
         */
        @Nullable R
        visitVariableAccessResource(TryStatement.VariableAccessResource var) throws EX;
    }

    /**
     * The visitor for the different kinds of {@link Java.Modifier}s.
     *
     * @param <R>  The type of the object returned by the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface ModifierVisitor<R, EX extends Throwable> extends AnnotationVisitor<R, EX> {

        @Nullable R visitAccessModifier(AccessModifier am) throws EX; // SUPPRESS CHECKSTYLE Javadoc
    }
}
