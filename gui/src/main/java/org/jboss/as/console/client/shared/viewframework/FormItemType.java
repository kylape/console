/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.as.console.client.shared.viewframework;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.ListItem;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.ObservableFormItem;
import org.jboss.ballroom.client.widgets.forms.PropertyEditorFormItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.forms.UnitBoxItem;

/**
 * Handy set of classes that know how to make a FormItem for edit or display of typical
 * data types.  You extend these classes to add extra validation.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public enum FormItemType {

    TEXT(new TextItemFactory()),
    TEXT_BOX(new TextBoxItemFactory()),
    BYTE_UNIT(new ByteUnitItemFactory()),
    CHECK_BOX(new CheckBoxItemFactory()),
    LIST_BOX(new ListBoxItemFactory()),
    NUMBER_BOX(new NumberBoxItemFactory()),
    NUMBER_BOX_ALLOW_NEGATIVE(new NumberBoxItemFactory(true)),
    NUMBER_UNIT_BOX(new UnitBoxItemFactory()),
    UNITS(new UnitsItemFactory()),
    COMBO_BOX(new ComboBoxItemFactory()),
    ISOLATION_TYPES(new ComboBoxItemFactory(new String[] {"REPEATABLE_READ"})),
    EVICTION_STRATEGY_TYPES(new ComboBoxItemFactory(new String[] {"NONE", "LRU"})),
    TIME_UNITS(new ComboBoxItemFactory(new String[] {"DAYS", "HOURS", "MINUTES", "SECONDS", "MILLISECONDS", "NANOSECONDS"})),
    PROPERTY_EDITOR(new PropertyEditorItemFactory());

    private FormItemFactory factory;

    private FormItemType(FormItemFactory factory) {
        this.factory = factory;
    }

    public FormItemFactory getFactory() {
        return this.factory;
    }

    public static interface FormItemFactory {
        ObservableFormItem[] makeFormItem(PropertyBinding propBinding, FormItemObserver... observers);
    }

    public static class TextItemFactory implements FormItemFactory {
        @Override
        public ObservableFormItem[] makeFormItem(PropertyBinding propBinding, FormItemObserver... observers) {
            TextItem textItem = new TextItem(propBinding.getJavaName(), propBinding.getLabel());
            textItem.setRequired(propBinding.isRequired());
            return new ObservableFormItem[] {new ObservableFormItem(propBinding, textItem, observers)};
        }
    }

    public static class TextBoxItemFactory implements FormItemFactory {
        @Override
        public ObservableFormItem[] makeFormItem(PropertyBinding propBinding, FormItemObserver... observers) {
            TextBoxItem textBoxItem = new TextBoxItem(propBinding.getJavaName(), propBinding.getLabel());
            textBoxItem.setRequired(propBinding.isRequired());
            return new ObservableFormItem[] {new ObservableFormItem(propBinding, textBoxItem, observers)};
        }
    }

    public static class ByteUnitItemFactory implements FormItemFactory {
        @Override
        public ObservableFormItem[] makeFormItem(PropertyBinding propBinding, FormItemObserver... observers) {
            ByteUnitItem byteUnitItem = new ByteUnitItem(propBinding.getJavaName(), propBinding.getLabel());
            byteUnitItem.setRequired(propBinding.isRequired());
            return new ObservableFormItem[] {new ObservableFormItem(propBinding, byteUnitItem, observers)};
        }

        private static class ByteUnitItem extends TextBoxItem {
            private char[] UNIT_CHARS = {'b', 'k', 'm', 'g', 't'};

            public ByteUnitItem(String name, String title) {
                super(name, title);
            }

            @Override
            // GWT doesn't allow me to use regex to do this validation.
            // Compiler chokes on Pattern class.
            public boolean validate(String value) {
                if (!super.validate(name)) {
                    return false;
                }

                if (value.length() < 2) return invalidValue();

                char finalChar = value.toLowerCase().charAt(value.length() - 1);

                boolean foundUnit = false;
                for (char unit : UNIT_CHARS) {
                    if (unit == finalChar) foundUnit = true;
                }

                if (!foundUnit) return invalidValue();

                String number = value.substring(0, value.length() - 1);
                try {
                    Long.parseLong(number);
                } catch (NumberFormatException e) {
                    return invalidValue();
                }

                return true;
            }

            private boolean invalidValue() {
                this.errMessage = Console.CONSTANTS.subsys_logging_invalidByteSpec();
                return false;
            }
        }
    }

    public static class ComboBoxItemFactory implements FormItemFactory {
        private String[] values = new String[0];

        public ComboBoxItemFactory() {}

        public ComboBoxItemFactory(String[] values) {
            this.values = values;
        }

        @Override
        public ObservableFormItem[] makeFormItem(PropertyBinding propBinding, FormItemObserver... observers) {
            ComboBoxItem comboBoxItem = new ComboBoxItem(propBinding.getJavaName(), propBinding.getLabel());
            comboBoxItem.setRequired(propBinding.isRequired());
            comboBoxItem.setValueMap(values);
            return new ObservableFormItem[] {new ObservableFormItem(propBinding, comboBoxItem, observers)};
        }
    }

    public static class CheckBoxItemFactory implements FormItemFactory {
        @Override
        public ObservableFormItem[] makeFormItem(PropertyBinding propBinding, FormItemObserver... observers) {
            CheckBoxItem checkBoxItem = new CheckBoxItem(propBinding.getJavaName(), propBinding.getLabel());
            checkBoxItem.setRequired(propBinding.isRequired());
            return new ObservableFormItem[] {new ObservableFormItem(propBinding, checkBoxItem, observers)};
        }
    }

    public static class ListBoxItemFactory implements FormItemFactory {
        @Override
        public ObservableFormItem[] makeFormItem(PropertyBinding propBinding, FormItemObserver... observers) {
            ListItem listItem = new ListItem(propBinding.getJavaName(), propBinding.getLabel(), true);
            listItem.setRequired(propBinding.isRequired());
            return new ObservableFormItem[] {new ObservableFormItem(propBinding, listItem, observers)};
        }
    }

    /**
     * Factory for Short, Integer, and Long values
     */
    public static class NumberBoxItemFactory implements FormItemFactory {
        private boolean allowNegativeNumber;

        public NumberBoxItemFactory() {
            this(false);
        }

        public NumberBoxItemFactory(boolean allowNegativeNumber) {
            this.allowNegativeNumber = allowNegativeNumber;
        }

        @Override
        public ObservableFormItem[] makeFormItem(PropertyBinding propBinding, FormItemObserver... observers) {
            NumberBoxItem numberItem = new NumberBoxItem(propBinding.getJavaName(), propBinding.getLabel(), this.allowNegativeNumber);
            numberItem.setRequired(propBinding.isRequired());
            return new ObservableFormItem[] {new ObservableFormItem(propBinding, numberItem, observers)};
        }
    }

    public static class UnitBoxItemFactory implements FormItemFactory {
        @Override
        public ObservableFormItem[] makeFormItem(PropertyBinding propBinding, FormItemObserver... observers) {
            UnitBoxItem<Long> unitBoxItem = new UnitBoxItem<Long>(propBinding.getJavaName(), propBinding.getLabel(), Long.class);
            return new ObservableFormItem[] {new ObservableFormItem(propBinding, unitBoxItem, observers),
                new ObservableFormItem(propBinding, unitBoxItem.getUnitItem(), observers)};
        }
    }

    public static class UnitsItemFactory implements FormItemFactory {
        @Override
        public ObservableFormItem[] makeFormItem(PropertyBinding propBinding, FormItemObserver... observers) {
            // Units represent the unit part of a UnitBox, the form item is created as part of the UnitBoxItem
            return new ObservableFormItem[] {};
        }

    }

    public static class PropertyEditorItemFactory implements FormItemFactory {
        private String addDialogTitle;
        private int rows;

        public PropertyEditorItemFactory() {
            this(Console.CONSTANTS.common_label_addProperty(), 5);
        }

        /**
         * @param addDialogTitle The title shown when the Add button is pressed on the PropertyEditor.
         * @param rows The number of rows in the PropertyEditor.
         */
        public PropertyEditorItemFactory(String addDialogTitle, int rows) {
            this.addDialogTitle = addDialogTitle;
            this.rows = rows;
        }

        @Override
        public ObservableFormItem[] makeFormItem(PropertyBinding propBinding, FormItemObserver... observers) {
            PropertyEditorFormItem propEditor = new PropertyEditorFormItem(propBinding.getJavaName(),
                                                                           "",
                                                                           addDialogTitle,
                                                                           rows);
            propEditor.setRequired(propBinding.isRequired());
            return new ObservableFormItem[] {new ObservableFormItem(propBinding, propEditor, observers)};
        }
    }
}
