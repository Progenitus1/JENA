package cz.muni.jena.issue.language.elements;

import com.github.javaparser.ast.body.Parameter;

import java.util.Objects;

public record ParameterWrapper(Parameter parameter)
{
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        ParameterWrapper that = (ParameterWrapper) o;
        return Objects.equals(parameter.getName().asString(), that.parameter.getName().asString());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(parameter.getName().asString());
    }
}
