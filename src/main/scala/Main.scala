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

  println(result)
