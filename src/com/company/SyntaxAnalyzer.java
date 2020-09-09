package com.company;

import com.company.AST.*;
import com.company.tokens.Token;

import java.util.ArrayList;

public class SyntaxAnalyzer {
    private final ArrayList<Token> tokens;
    private int cursor;

    public SyntaxAnalyzer(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public Node makeAST() {
        cursor = 0;
        return parseProgram();
    }

    private boolean cursorAtToken(String type) {
        return tokens.get(cursor).getType().equals(type);
    }

    private boolean checkKeyword(String keyword) {
        if (!cursorAtToken(keyword)) {
            return false;
        }
        //System.out.println("Keyword "+keyword+ ", cursor = " + cursor);
        cursor++;
        return true;
    }

    private Node parseProgram() {
        Node currentNode = new Node(NodeType.Program, null);
        while (true) {
            Node child = parseClassDeclaration(currentNode);
            if (child == null)
                break;
            currentNode.addChild(child);
        }
        return currentNode;
    }

    private Node parseClassDeclaration(Node parent) {
        Node currentNode = new Node(NodeType.ClassDeclaration, parent);
        if (cursor == tokens.size())
            return null;
        //Check "class" keyword
        if (!checkKeyword("class"))
            return null;
        //Parse className
        Node className = parseClassName(currentNode);
        if (className == null) {
            System.out.println("Syntax error: No classname in class declaration");
            return null;
        }
        currentNode.addChild(className);
        //Parse extendedClassName if there is any
        if (checkKeyword("extends")) {
            Node extendedClass = parseClassName(currentNode);
            if (extendedClass == null) {
                System.out.println("Syntax error: No extended class name in class declaration");
                return null;
            }
            currentNode.addChild(extendedClass);
        } else {
            //Fill the gap
            currentNode.addChild(null);
        }
        //Check "is" keyword
        if (!checkKeyword("is"))
            return null;
        //Parse memberDeclarations
        while (true) {
            Node child = parseMemberDeclaration(currentNode);
            if (child == null)
                break;
            currentNode.addChild(child);
        }
        //Check "end" keyword
        if (!checkKeyword("end"))
            return null;
        return currentNode;
    }

    private Node parseClassName(Node parent) {
        Node currentNode = new Node(NodeType.ClassName, parent);
        //Parse className
        Node classNameId = parseIdentifier(parent);
        if (classNameId == null) {
            return null;
        }
        currentNode.addChild(classNameId);
        //Parse genericClassName
        if (checkKeyword("openSquareBracketSeparator")) {
            Node genericClassName = parseClassName(currentNode);
            if (genericClassName == null) {
                System.out.println("Syntax error: There is no expected genericClassName found");
                return null;
            }
            currentNode.addChild(genericClassName);
            if (!checkKeyword("closeSquareBracketSeparator"))
                return null;
        }
        return currentNode;
    }

    private Node parseMemberDeclaration(Node parent) {
        Node currentNode = new Node(NodeType.MemberDeclaration, parent);
        //Check whether declaration is VariableDeclaration, MethodDeclaration or ConstructorDeclaration
        Node declaration = parseVariableDeclaration(currentNode);
        if (declaration == null) {
            declaration = parseMethodDeclaration(currentNode);
            if (declaration == null) {
                declaration = parseConstructorDeclaration(currentNode);
                if (declaration == null) {
                    //There is no declaration
                    return null;
                }
            }
        }
        currentNode.addChild(declaration);
        return currentNode;
    }

    private Node parseVariableDeclaration(Node parent) {
        Node currentNode = new Node(NodeType.VariableDeclaration, parent);
        //Check "var" keyword
        if (!checkKeyword("var")) {
            return null;
        }
        //Parse identifier
        Node id = parseIdentifier(currentNode);
        if (id == null) {
            System.out.println("Syntax error: There is no identifier in variable declaration");
            return null;
        }
        currentNode.addChild(id);
        //Check varAssignOperator ":"
        if (!checkKeyword("typeAssignOperator")) {
            return null;
        }
        //Parse expression
        Node exp = parseExpression(currentNode);
        if (exp == null) {
            System.out.println("Syntax error: There is no expression in variable declaration");
            return null;
        }
        currentNode.addChild(exp);
        return currentNode;
    }

    private Node parseMethodDeclaration(Node parent) {
        Node currentNode = new Node(NodeType.MethodDeclaration, parent);
        //Parse "method" keyword
        if (!checkKeyword("method"))
            return null;
        //Parse identifier
        Node id = parseIdentifier(currentNode);
        if (id == null) {
            System.out.println("Syntax error: There is no identifier in method declaration");
            return null;
        }
        currentNode.addChild(id);
        //Parse parameters
        Node params = parseParameters(currentNode);
        currentNode.addChild(params);
        //Parse ":"
        if (checkKeyword("typeAssignOperator")) {
            Node type = parseIdentifier(currentNode);
            if (type == null) {
                System.out.println("Syntax error: There is no type in method declaration");
                return null;
            }
            currentNode.addChild(type);
        } else {
            currentNode.addChild(null);
        }
        //Parse "is" keyword
        if (!checkKeyword("is"))
            return null;
        //Parse body
        Node body = parseBody(currentNode);
        currentNode.addChild(body);
        //Parse "end" keyword
        if (!checkKeyword("end"))
            return null;
        return currentNode;
    }

    private Node parseParameters(Node parent) {
        Node currentNode = new Node(NodeType.Parameters, parent);
        //Parse parameter declaration
        if (!checkKeyword("openBracketSeparator"))
            return null;
        Node parameterDeclaration = parseParameterDeclaration(currentNode);
        if (parameterDeclaration == null) {
            return null;
        }
        currentNode.addChild(parameterDeclaration);
        //Parse additional parameters
        while (checkKeyword("commaSeparator")) {
            Node addParameterDecl = parseParameterDeclaration(currentNode);
            if (addParameterDecl == null) {
                System.out.println("Syntax error: Expected additional parameter is missing in method declaration");
                return null;
            }
            currentNode.addChild(addParameterDecl);
        }
        if (!checkKeyword("closeBracketSeparator"))
            return null;
        return currentNode;
    }

    private Node parseConstructorDeclaration(Node parent) {
        Node currentNode = new Node(NodeType.ConstructorDeclaration, parent);
        //Parse "this" keyword
        if (!checkKeyword("this"))
            return null;
        //Parse parameters
        Node params = parseParameters(currentNode);
        currentNode.addChild(params);
        //Parse "is" keyword
        if (!checkKeyword("is"))
            return null;
        //Parse body
        Node body = parseBody(currentNode);
        currentNode.addChild(body);
        //Parse "end" keyword
        if (!checkKeyword("end"))
            return null;
        return currentNode;
    }

    private Node parseStatement(Node parent) {
        Node currentNode = new Node(NodeType.Statement, parent);
        //Parse available statements
        //return null if there is none
        Node statement = parseAssignment(currentNode);
        if (statement == null) {
            statement = parseWhileLoop(currentNode);
            if (statement == null) {
                statement = parseIfStatement(currentNode);
                if (statement == null) {
                    statement = parseReturnStatement(currentNode);
                    if (statement == null) {
                        statement = parseExpression(currentNode);
                        if (statement == null)
                            return null;
                    }
                }
            }
        }
        currentNode.addChild(statement);
        return currentNode;
    }

    private Node parseReturnStatement(Node parent) {
        Node currentNode = new Node(NodeType.ReturnStatement, parent);
        //Parse "return" keyword
        if (!checkKeyword("return"))
            return null;
        //Parse expression
        Node expression = parseExpression(currentNode);
        currentNode.addChild(expression);
        return currentNode;
    }

    private Node parseAssignment(Node parent) {
        Node currentNode = new Node(NodeType.Assignment, parent);
        //Parse identifier
        Node identifier = parseIdentifier(currentNode);
        if (identifier == null) {
            return null;
        }
        currentNode.addChild(identifier);

        if (!checkKeyword("varAssignOperator"))
            return null;

        Node expression = parseExpression(currentNode);
        if (expression == null) {
            return null;
        }
        currentNode.addChild(expression);
        return currentNode;
    }

    private Node parseBody(Node parent) {
        Node currentNode = new Node(NodeType.Body, parent);
        while (true) {
            Node varDeclOrStatement;
            varDeclOrStatement = parseVariableDeclaration(currentNode);
            if (varDeclOrStatement != null) {
                currentNode.addChild(varDeclOrStatement);
            } else {
                varDeclOrStatement = parseStatement(currentNode);
                if (varDeclOrStatement != null) {
                    currentNode.addChild(varDeclOrStatement);
                } else {
                    break;
                }
            }
        }
        return currentNode;
    }

    private Node parseParameterDeclaration(Node parent) {
        Node currentNode = new Node(NodeType.ParameterDeclaration, parent);
        //Parse identifier
        Node identifier = parseIdentifier(currentNode);
        if (identifier == null) {
            return null;
        }
        currentNode.addChild(identifier);

        if (!checkKeyword("typeAssignOperator"))
            return null;

        //Parse className
        Node className = parseClassName(currentNode);
        if (className == null) {
            return null;
        }
        currentNode.addChild(className);

        return currentNode;
    }

    private Node parseWhileLoop(Node parent) {
        Node currentNode = new Node(NodeType.WhileLoop, parent);
        if (!checkKeyword("while"))
            return null;
        //Parse expression
        Node expression = parseExpression(currentNode);
        if (expression == null) {
            return null;
        }
        currentNode.addChild(expression);
        //Parse loop keyword
        if (!checkKeyword("loop"))
            return null;
        //Parse body
        Node body = parseBody(currentNode);
        currentNode.addChild(body);

        if (!checkKeyword("end"))
            return null;
        return currentNode;
    }

    private Node parseIfStatement(Node parent) {
        Node currentNode = new Node(NodeType.IfStatement, parent);
        if (!checkKeyword("if"))
            return null;
        //Parse expression
        Node expression = parseExpression(currentNode);
        if (expression == null) {
            return null;
        }
        currentNode.addChild(expression);
        //Parse then keyword
        if (!checkKeyword("then"))
            return null;
        //Parse body
        Node body = parseBody(currentNode);
        currentNode.addChild(body);
        if (checkKeyword("else")) {
            //Else body
            Node elsebody = parseBody(currentNode);
            currentNode.addChild(elsebody);
        }
        //Parse end
        if (!checkKeyword("end"))
            return null;
        return currentNode;
    }

    private Node parseExpression(Node parent) {
        Node currentNode = new Node(NodeType.Expression, parent);
        //Parse primary
        Node primary = parsePrimary(currentNode);
        if (primary == null) {
            return null;
        }
        currentNode.addChild(primary);
        //Parse method calls
        while (true) {
            Node methodCall = parseMethodCall(currentNode);
            if (methodCall == null)
                break;
            currentNode.addChild(methodCall);
        }
        return currentNode;
    }

    private Node parseArguments(Node parent) {
        Node currentNode = new Node(NodeType.Arguments, parent);
        if (!checkKeyword("openBracketSeparator"))
            return null;
        //Parse expression
        Node expression = parseExpression(currentNode);
        if (expression == null) {
            return null;
        }
        currentNode.addChild(expression);
        while (checkKeyword("commaSeparator"))
        {
            //Parse expression
            Node addExpression = parseExpression(currentNode);
            if (addExpression==null)
            {
                System.out.println("Syntax error: additional argument is missing");
                return null;
            }
            currentNode.addChild(addExpression);
        }
        if (!checkKeyword("closeBracketSeparator"))
            return null;
        return currentNode;
    }

    private Node parseMethodCall(Node parent) {
        Node currentNode = new Node(NodeType.MethodCall, parent);
        if (!checkKeyword("dotSeparator"))
            return null;
        //Parse identifier
        Node identifier = parseIdentifier(currentNode);
        if (identifier == null) {
            return null;
        }
        currentNode.addChild(identifier);
        //Parse arguments
        Node arguments = parseArguments(currentNode);
        if (arguments == null) {
            return null;
        }
        currentNode.addChild(arguments);
        return currentNode;
    }

    private Node parseIdentifier(Node parent) {
        Node currentNode = new Node(NodeType.Identifier, parent, tokens.get(cursor));
        if (cursorAtToken("identifier")) {
            //System.out.println("Identifier + "+tokens.get(cursor).getType() + ", cursor = " + cursor);
            cursor++;
            return currentNode;
        } else
            return null;
    }

    private Node parsePrimary(Node parent) {
        Node currentNode = new Node(NodeType.Primary, parent);
        if (cursorAtToken("real") ||
                cursorAtToken("integer") ||
                cursorAtToken("this")) {
            cursor++;
            //System.out.println("Primary + "+tokens.get(cursor).getType() +", cursor = " + cursor);
            return new Node(NodeType.MethodCall, parent, tokens.get(cursor));
        } else {
            Node classNameOrIdentifier = parseIdentifier(currentNode);
            if (classNameOrIdentifier == null) {
                classNameOrIdentifier = parseClassName(currentNode);
                if (classNameOrIdentifier == null)
                    return null;
            }
            currentNode.addChild(classNameOrIdentifier);
        }
        return currentNode;
    }
}
