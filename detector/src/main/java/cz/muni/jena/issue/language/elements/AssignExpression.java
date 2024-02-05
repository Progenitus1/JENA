package cz.muni.jena.issue.language.elements;

import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Map.entry;

public record AssignExpression(AssignExpr assignExpr)
{

    public boolean isFieldAssignedInCallable(CallableDeclaration<? extends CallableDeclaration<?>> callableDeclaration)
    {
        return findAssignedFieldsFromParameter(callableDeclaration).findFirst().isPresent();
    }

    public Stream<Map.Entry<Parameter, ResolvedFieldDeclaration>> findAssignedFieldsFromParameter(CallableDeclaration<? extends CallableDeclaration<?>> callableDeclaration)
    {
        return findFieldAssignment()
                .map(assignment -> callableDeclaration.getParameterByName(
                        assignment.assignExpr.getValue().asNameExpr().getName().asString()
                        )
                        .map(parameter -> entry(parameter, assignment.resolveTargetField())
                ))
                .flatMap(Optional::stream);
    }

    private Stream<AssignExpression> findFieldAssignment()
    {
        return Stream.of(this)
                .filter(AssignExpression::isTargetField)
                .filter(assignment -> assignment.assignExpr.getValue().isNameExpr());
    }

    public Stream<AssignExpr> findFieldAssigmentOfInjectedField(Set<String> injectedFieldsNames)
    {
        return findFieldAssignment()
                .filter(
                        assigment -> injectedFieldsNames.contains(assigment.resolveTargetField().getName())
                ).map(AssignExpression::assignExpr);
    }

    public boolean isTargetField()
    {
        return isTargetFieldsNameExpr() || isTargetFieldAccessExpr();
    }

    private boolean isTargetFieldAccessExpr()
    {
        return Stream.of(assignExpr.getTarget())
                .filter(Expression::isFieldAccessExpr)
                .map(Expression::asFieldAccessExpr)
                .flatMap(ResolvableNode::resolve)
                .anyMatch(ResolvedValueDeclaration::isField);
    }

    private boolean isTargetFieldsNameExpr()
    {
        return Stream.of(assignExpr.getTarget()).filter(Expression::isNameExpr)
                .map(Expression::asNameExpr)
                .flatMap(ResolvableNode::resolve)
                .anyMatch(ResolvedValueDeclaration::isField);
    }

    public ResolvedFieldDeclaration resolveTargetField()
    {
        Stream<ResolvedValueDeclaration> fieldDeclaration = Stream.empty();
        if (isTargetFieldAccessExpr())
        {
            fieldDeclaration = ResolvableNode.resolve(assignExpr.getTarget().asFieldAccessExpr());
        }
        if (isTargetFieldsNameExpr())
        {
            fieldDeclaration = ResolvableNode.resolve(assignExpr.getTarget().asNameExpr());
        }
        return fieldDeclaration.map(ResolvedDeclaration::asField).findFirst().orElseThrow();
    }
}
