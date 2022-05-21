import io.getquill.*
import io.getquill.context.ExecutionInfo

import javax.sql.DataSource

case class Foo(id: Int, attribute: String)
case class Bar(id: Int, fooId: Int, attribute: String)

@main def hello: Unit =
  val ctx = new H2JdbcContext(SnakeCase, "h2")
  import ctx.*

  executeAction("create table foo (id int primary key, attribute varchar(255));")(ExecutionInfo.unknown, ())
  executeAction("create table bar (id int primary key, foo_id int, attribute varchar(255));")(ExecutionInfo.unknown, ())

  run(query[Foo].insertValue(Foo(1, "a")))
  run(query[Foo].insertValue(Foo(2, "b")))
  run(query[Bar].insertValue(Bar(1, 1, "c")))
  run(query[Bar].insertValue(Bar(2, 1, "d")))

  val result = run(for {
    foo <- query[Foo]
    bar <- query[Bar].leftJoin(bar => bar.fooId == foo.id)
  } yield (foo, bar))
  // [info] 21 |  val result = run(for {
  // [info]    |               ^
  // [info]    |Quill Query: SELECT foo.id, foo.attribute, bar.id, bar.foo_id AS fooId, bar.attribute FROM foo foo LEFT JOIN bar bar ON bar.foo_id = foo.id
  // [info] 22 |    foo <- query[Foo]
  // [info] 23 |    bar <- query[Bar].leftJoin(bar => bar.fooId == foo.id)
  // [info] 24 |  } yield (foo, bar))
  // [WARNING] Arity of product column "_2" type Option[Bar] was 0. This is not valid.

  println(result)
  // gives: List((Foo(1,a),Some(Bar(1,1,c))), (Foo(1,a),Some(Bar(2,1,d))), (Foo(2,b),Some(Bar(0,0,null))))
