/*
 * Copyright (C) 2016 Worker Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.raatiniemi.worker.domain.repository.query;

/**
 * Criteria used for matching values.
 * <p/>
 * TODO: Add support for additional operators.
 */
public class Criteria {
    /**
     * Name of the criteria field.
     */
    private final String mField;

    /**
     * Operator to use.
     */
    private final String mOperator;

    /**
     * Value to match against the field.
     */
    private final String mValue;

    /**
     * Constructor.
     *
     * @param field    Name of the criteria field.
     * @param operator Operator to use.
     * @param value    Value to match against the field.
     */
    private Criteria(final String field, final String operator, final String value) {
        mField = field;
        mOperator = operator;
        mValue = value;
    }

    /**
     * Create a criteria where the field is equal to the value.
     *
     * @param field Name of the criteria field.
     * @param value Value to match against the field.
     * @param <T>   Type reference for the value.
     * @return Criteria for field equal to the value.
     */
    public static <T> Criteria equalTo(final String field, final T value) {
        return new Criteria(field, "=", String.valueOf(value));
    }

    /**
     * Get the name of the criteria field.
     *
     * @return Name of the criteria field.
     */
    public String getField() {
        return mField;
    }

    /**
     * Get the operator to use.
     *
     * @return Operator to use.
     */
    public String getOperator() {
        return mOperator;
    }

    /**
     * Get the value to match against the field.
     *
     * @return Value to match against the field.
     */
    public String getValue() {
        return mValue;
    }

    @Override
    public String toString() {
        return getField() + getOperator() + getValue();
    }
}
