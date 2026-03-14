package org.strawberryfoundations.wear.calculator.presentation.core

enum class Keypad(val label: String, val isOperator: Boolean = false) {
    KEY_1("1"),
    KEY_2("2"),
    KEY_3("3"),
    KEY_4("4"),
    KEY_5("5"),
    KEY_6("6"),
    KEY_7("7"),
    KEY_8("8"),
    KEY_9("9"),
    KEY_0("0"),
    KEY_PLUS("+", isOperator = true),
    KEY_MINUS("−", isOperator = true),
    KEY_MULTIPLY("×", isOperator = true),
    KEY_DIVIDE("÷", isOperator = true),
    KEY_EQUALS("=", isOperator = true),
    KEY_DECIMAL(".");
}