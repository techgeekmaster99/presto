/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.operator.scalar.distinct;

import com.facebook.presto.common.type.DistinctType;
import com.facebook.presto.common.type.Type;
import com.facebook.presto.metadata.BoundVariables;
import com.facebook.presto.metadata.FunctionAndTypeManager;
import com.facebook.presto.metadata.SqlOperator;
import com.facebook.presto.operator.scalar.BuiltInScalarFunctionImplementation;
import com.facebook.presto.spi.function.FunctionHandle;
import com.google.common.collect.ImmutableList;

import java.util.Optional;

import static com.facebook.presto.common.function.OperatorType.HASH_CODE;
import static com.facebook.presto.common.type.StandardTypes.BIGINT;
import static com.facebook.presto.common.type.StandardTypes.DISTINCT_TYPE;
import static com.facebook.presto.common.type.TypeSignature.parseTypeSignature;
import static com.facebook.presto.operator.scalar.ScalarFunctionImplementationChoice.ArgumentProperty.valueTypeArgumentProperty;
import static com.facebook.presto.operator.scalar.ScalarFunctionImplementationChoice.NullConvention.RETURN_NULL_ON_NULL;
import static com.facebook.presto.spi.function.Signature.withVariadicBound;
import static com.facebook.presto.sql.analyzer.TypeSignatureProvider.fromTypes;

public class DistinctTypeHashCodeOperator
        extends SqlOperator
{
    public static final DistinctTypeHashCodeOperator DISTINCT_TYPE_HASH_CODE_OPERATOR = new DistinctTypeHashCodeOperator();

    private DistinctTypeHashCodeOperator()
    {
        super(HASH_CODE,
                ImmutableList.of(withVariadicBound("T", DISTINCT_TYPE)),
                ImmutableList.of(),
                parseTypeSignature(BIGINT),
                ImmutableList.of(parseTypeSignature("T")));
    }

    @Override
    public BuiltInScalarFunctionImplementation specialize(BoundVariables boundVariables, int arity, FunctionAndTypeManager functionAndTypeManager)
    {
        DistinctType type = (DistinctType) boundVariables.getTypeVariable("T");
        Type baseType = type.getBaseType();
        FunctionHandle functionHandle = functionAndTypeManager.resolveOperator(HASH_CODE, fromTypes(baseType));

        return new BuiltInScalarFunctionImplementation(
                false,
                ImmutableList.of(valueTypeArgumentProperty(RETURN_NULL_ON_NULL)),
                functionAndTypeManager.getJavaScalarFunctionImplementation(functionHandle).getMethodHandle(),
                Optional.empty());
    }
}
