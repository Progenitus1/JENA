package cz.muni.jena.issue.language.elements;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.resolution.declarations.ResolvedDeclaration;

import java.util.Collection;
import java.util.stream.Stream;

public record ReturnStatement(ReturnStmt returnStmt)
{
    public boolean isInjectedFieldReturned(Collection<String> injectedFieldsNames)
    {
        return returnStmt.getExpression().map(
                expression -> Stream.of(expression)
                        .filter(Expression::isNameExpr)
                        .map(Expression::asNameExpr)
                        .flatMap(ResolvableNode::resolve)
                        .filter(ResolvedDeclaration::isField)
                        .map(ResolvedDeclaration::getName)
                        .anyMatch(injectedFieldsNames::contains)
        ).orElse(false);
    }

    public ReturnStmt getReturnStmt()
    {
        return returnStmt;
    }
}
