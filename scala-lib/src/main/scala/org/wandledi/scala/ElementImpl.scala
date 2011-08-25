package org.wandledi.scala

import org.xml.sax.Attributes
import org.wandledi.Attribute
import org.wandledi.Scroll
import org.wandledi.Selector
import org.wandledi.Spell
import org.wandledi.ElementForeachImpl
import org.wandledi.Plan
import org.wandledi.spells.InsertionIntent
import org.wandledi.spells.ReplacementIntent
import org.wandledi.spells.StringTransformation
import org.wandledi.spells.TransformedAttribute

class ElementImpl(
  aSelector: Selector,
  aScroll: Scroll)
extends org.wandledi.ElementImpl(aSelector, aScroll) with Element {

  def text_=(value: String) {
    text.setContent(value)
  }
  def text: TextContent = new TextContentImpl(this)

  def foreachIn[T: ClassManifest](items: Iterable[T], reduceBefore: Boolean = false)
      (fun: (SelectableElement, T) => Unit) {
    import collection.JavaConversions._
    val plan = new Plan[T] {
      def execute(e: org.wandledi.SelectableElement, item: T): Unit =
        fun(new SelectableElementImpl(e.getSelector, e.getScroll, e.getScroll), item)
    }
    val foreach = new ElementForeachImpl(this, items, reduceBefore)
    foreach.apply(plan)
  }

  def foreachWithIndexIn[T: ClassManifest](items: Iterable[T], reduceBefore: Boolean = false)
      (fun: (SelectableElement, T, Int) => Unit) {
    import collection.JavaConversions._
    val plan = new Plan[T] {
      def execute(e: org.wandledi.SelectableElement, item: T): Unit =
        fun(new SelectableElementImpl(e.getSelector, e.getScroll, e.getScroll), item, index)
    }
    val foreach = new ElementForeachImpl(this, items, reduceBefore)
    foreach.apply(plan)
  }

  def changeAttribute(name: String, change: (String) => String) {
    changeAttribute(name, new StringTransformation {
      def transform(value: String) = change(value)
    })
  }

  def changeAttributes(attr: (String, (String) => String)*) {
    val changes = attr.map { case (name, change) =>
      new TransformedAttribute(name, new StringTransformation {
          def transform(value: String) = change(value)
        })
    }
    changeAttributes(changes: _*)
  }

  def setAttributes(attr: (String, String)*) {
    setAttributes(attr.map(t => new Attribute(t._1, t._2)): _*)
  }

  def includeFile(file: String)(magic: (Selectable) => Unit) {
    val scroll = new Scroll
    magic(new SelectableImpl(scroll))
    includeFile(file, scroll)
  }

  def insert(atEnd: Boolean)(insertion: (Spell) => Unit) {
    val intent = new InsertionIntent {
      def insert(parent: Spell) {
        insertion(parent)
      }
    }
    insert(atEnd, intent)
  }

  def insert(atEnd: Boolean = false, insertion: xml.NodeSeq) {
    insert(insertion.toString, atEnd)
  }

  def replace(contentsOnly: Boolean, replacement: (String, Attributes, Spell) => Unit) {
    val intent = new ReplacementIntent {
      def replace(label: String, attributes: Attributes, parent: Spell) {
        replacement(label, attributes, parent)
      }
    }
    replace(contentsOnly, intent)
  }

  def replace(contentsOnly: Boolean)(replacement: (String, Attributes) => xml.NodeSeq) {
    val intent = new ReplacementIntent {
      def replace(label: String, attributes: Attributes, parent: Spell) {
        parent.writeString(replacement(label, attributes).toString)
      }
    }
    replace(contentsOnly, intent)
  }

  def replace(contentsOnly: Boolean, replacement: xml.NodeSeq) {
    replace(contentsOnly, replacement.toString)
  }
}
