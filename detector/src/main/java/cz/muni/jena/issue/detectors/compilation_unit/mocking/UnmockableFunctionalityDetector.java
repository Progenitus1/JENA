package cz.muni.jena.issue.detectors.compilation_unit.mocking;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithFinalModifier;
import com.github.javaparser.resolution.Resolvable;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodLikeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import cz.muni.jena.configuration.mocking.MockingConfiguration;
import cz.muni.jena.issue.Issue;
import cz.muni.jena.issue.IssueCategory;
import cz.muni.jena.issue.IssueType;
import cz.muni.jena.issue.language.elements.Class;
import cz.muni.jena.issue.language.elements.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component("staticMethodDetector")
public class UnmockableFunctionalityDetector implements MockingIssueDetector
{
    @Override
    public @NonNull IssueCategory getIssueCategory()
    {
        return IssueCategory.MOCKING;
    }

    @Override
    public @NonNull Stream<Issue> findIssues(
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration,
            MockingConfiguration configuration
    )
    {
        return Stream.concat(
                        classOrInterfaceDeclaration.findAll(MethodCallExpr.class).stream()
                                .flatMap(CallableCallWithIssueType::fromMethodCall),
                        classOrInterfaceDeclaration.findAll(ObjectCreationExpr.class).stream()
                                .map(objectCreationExpr -> new CallableCallWithIssueType<>(
                                        objectCreationExpr,
                                        IssueType.CONSTRUCTOR_CALL_WITH_EXCEPTION
                                ))
                )
                .filter(
                        callableCallWithIssueType -> Stream.<Resolvable<? extends ResolvedMethodLikeDeclaration>>
                                        of(callableCallWithIssueType.callableCall)
                                .flatMap(ResolvableNode::resolve)
                                .map(CallableDec::getSpecifiedException)
                                .flatMap(List::stream)
                                .filter(ResolvedType::isReferenceType)
                                .map(ResolvedType::asReferenceType)
                                .map(ResolvedReferenceType::getTypeDeclaration)
                                .flatMap(Optional::stream)
                                .filter(ResolvedTypeDeclaration::isClass)
                                .map(ResolvedTypeDeclaration::asClass)
                                .map(ResolvedClassDec::new)
                                .anyMatch(
                                        resolvedClassDec -> resolvedClassDec
                                                .isAnyOfTheseClassesInHierarchy(configuration.exceptions())
                                )
                )
                .map(callableCallWithIssueType -> Issue.fromNodeWithRange(
                        callableCallWithIssueType.callableCall,
                        callableCallWithIssueType.issueType,
                        classOrInterfaceDeclaration
                ));
    }

    private record CallableCallWithIssueType<T extends Resolvable<? extends ResolvedMethodLikeDeclaration>>(
            T callableCall,
            IssueType issueType
    )
    {
        public static Stream<CallableCallWithIssueType<MethodCallExpr>> fromMethodCall(MethodCallExpr methodCallExpr)
        {
            Optional<ResolvedMethodDeclaration> resolvedMethodDec = ResolvableNode.resolve(methodCallExpr).findFirst();
            if (resolvedMethodDec.map(ResolvedMethodDeclaration::isStatic).orElse(false))
            {
                return Stream.of(new CallableCallWithIssueType<>(
                        methodCallExpr,
                        IssueType.STATIC_METHOD_CALL_WITH_EXCEPTION
                ));
            }
            if (
                    resolvedMethodDec.map(
                            resolvedMethodDeclaration -> resolvedMethodDeclaration.toAst(MethodDeclaration.class)
                                    .map(NodeWithFinalModifier::isFinal)
                                    .orElse(false)
                                    || Class.isFinalClass(resolvedMethodDec.map(MethodCall::declaringType).stream())
                    ).orElse(false)
            )
            {
                return Stream.of(new CallableCallWithIssueType<>(
                        methodCallExpr,
                        IssueType.FINAL_METHOD_CALL_WITH_EXCEPTION
                ));
            }
            return Stream.of();
        }
    }
}
