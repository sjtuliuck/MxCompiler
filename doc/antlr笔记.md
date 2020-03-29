# Antlr笔记

## 整合到自己的程序中

运行 ANTLR 4 会生成以下文件：

* `Lexer.java`: Lexer
* `Parser.java`: Parser
* `Listener.java`: Listener 接口
* `BaseListener.java`: Listener 默认实现
* `Visitor.java`: Visitor 接口
* `BaseVisitor.java`: Visitor 默认实现
* `[Lexer].tokens`: 当语法被拆分成多个多个文件时用于同步编号

**使用方法就是把 `*.java` 复制到项目中合适的位置，然后编写调用代码、Visitor及（或）Listener。**

## 调用代码

 使用方法：把输入流包装一下喂给Lexer，之后将Token流喂给Parser，最后调用ParseTree::< starting>生成解析树。

## Visitor模式

### Visitor的好处：把结构和行为分离

**Separate an algorithm from an object structure** 

* Specify processing methods for different types
* Walk over the tree in the correct order
* Check the argument for each node
* Control how child nodes are visited during the walk

* Implement:
  * Pretty-Print, Scope-Building, Type-checking ...
  * From CST to AST
    * Visitor
    * Listener: walker, enterNode(), exitNode()

### 不改动结构的好处
第一， Java 中每个 public 类都需要独立成一个文件，如果要在每个类里面都加上这么个行为，那么就需要分别打开一个个文件，与此同时这个行为的代码也被拆散到了一个个文件中，这无疑是非常不利于维护的。第二，有些情况下，我们对结构代码没有控制权，这个时候我们就不能往里面加代码了。

要增加一个行为，我们需要做的只是增加一个Visitor，在这个Visitor里面实现所有类的对应的行为即可。程序的其余部分完全不需要管。

## 使用 ANTLR 4 中的 Visitor 模式
* ctx.< nonterminal>() 可以访问语法规则中的 < nonterminal> 部分的 Context
* ctx.getText() 可以获得在原文中的串