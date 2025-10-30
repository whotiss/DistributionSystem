package app.domain.core

trait Repository[T]:
  def add(item: T): Unit
  def update(item: T): Unit
  def remove(id: String): Boolean
  def findById(id: String): Option[T]
  def findAll(): Seq[T]
