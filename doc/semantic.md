# Semantic

## 语义分析相关的类

* LocalResolver
* DereferenceChecker
* TypeResolver
* TypeChecker (visitor)
* TypeTable

## Scope

* Scope：表示作用域的抽象类
* GeneralScope：表示程序顶层的作用域。保存有函数和全局变量。
* LocalScope：表示一个临时变量的作用域。保存有形参和临时变量。（利用Stack）

## Resolver

---

* 将变量的名称和实体进行关联（消解）
* 将类型的名称（TypeRef）和实体进行关联（消解）
* 类型定义的检查（mx要求不高，忽略）
* 表达式的有效性检查 DereferenceChecker
* 静态类型检查 TypeChecker