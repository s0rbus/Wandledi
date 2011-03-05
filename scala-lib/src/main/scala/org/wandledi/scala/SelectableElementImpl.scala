package org.wandledi.scala

import org.xml.sax.Attributes
import org.wandledi.Scroll
import org.wandledi.Selector
import org.wandledi.Spell

class SelectableElementImpl(
  selector: Selector,
  parentScroll: Scroll,
  localScroll: Scroll
) extends org.wandledi.SelectableElementImpl(selector, parentScroll, localScroll) with SelectableElement {

  private val selectable = new SelectableImpl(localScroll)

  override def get(selector: Selector): Element = selectable.get(selector)
  override def get(atts: Tuple2[String, String]*): Element = selectable.get(atts: _*)
  override def get(label: String, atts: Tuple2[String, String]*): Element = selectable.get(label, atts: _*)
  override def get(selector: String): Element = selectable.get(selector)
  override def at(selector: Selector) = selectable.at(selector)
  override def at(selector: String) = selectable.at(selector)

  private val element = new ElementImpl(selector, parentScroll)

  def foreachIn[T: ClassManifest](items: Iterable[T])(fun: (SelectableElement, T) => Unit) =
    element.foreachIn(items)(fun)
  def foreachWithIndexIn[T: ClassManifest](items: Iterable[T])
      (fun: (SelectableElement, T, Int) => Unit) = element.foreachWithIndexIn(items)(fun)
  def changeAttribute(name: String)(change: (String) => String) =
    element.changeAttribute(name)(change)
  def includeFile(file: String)(magic: (Selectable) => Unit) =
    element.includeFile(file)(magic)
  
  def insert(atEnd: Boolean)(insertion: (Spell) => Unit) =
    element.insert(atEnd)(insertion)
  def insert(atEnd: Boolean = false, insertion: xml.NodeSeq) =
    element.insert(atEnd, insertion)
  
  def replace(contentsOnly: Boolean)(replacement: (String, Attributes, Spell) => Unit) =
    element.replace(contentsOnly)(replacement)
  def replace(contentsOnly: Boolean, replacement: (String, Attributes) => xml.NodeSeq) =
    element.replace(contentsOnly, replacement)
  def replace(contentsOnly: Boolean = true, replacement: xml.NodeSeq) =
    element.replace(contentsOnly, replacement)

  def text_=(value: String) = element.text_=(value)
  def text: TextContent = element.text
}