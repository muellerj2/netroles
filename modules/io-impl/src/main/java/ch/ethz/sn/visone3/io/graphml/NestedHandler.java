/*
 * This file is part of netroles.
 *
 * netroles is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * netroles is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with visone3.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.ethz.sn.visone3.io.graphml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/*
 * TODO stack with current instead of traverse.
 */
public abstract class NestedHandler extends DefaultHandler {
  private static final Logger LOG = LoggerFactory.getLogger(NestedHandler.class);
  final String indent;
  final String tag;
  final NestedHandler parent;
  final Map<String, Function<NestedHandler, NestedHandler>> childs = new HashMap<>();
  final Map<String, NestedHandler> cache = new HashMap<>();
  NestedHandler current;

  NestedHandler(final String indent) {
    tag = null;
    parent = null;
    this.indent = indent;
  }

  NestedHandler(final String tag, final NestedHandler parent) {
    this.tag = tag;
    this.parent = parent;
    indent = parent.indent + "  ";
  }

  @Override
  public final void startElement(
    final String uri, final String localName, final String qName, final Attributes attributes
  ) {
    if (current != null) {
      current.startElement(uri, localName, qName, attributes);
      return;
    }
    for (final Map.Entry<String, NestedHandler> e : cache.entrySet()) {
      if (e.getKey().equals(qName)) {
        final NestedHandler child = e.getValue();
        child.init(uri, localName, qName, attributes);
        LOG.trace("{}{{ {}", indent, child);
        current = child;
        return;
      }
    }
    for (final Map.Entry<String, Function<NestedHandler, NestedHandler>> e : childs.entrySet()) {
      if (e.getKey().equals(qName)) {
        final NestedHandler child = e.getValue().apply(this);
        child.init(uri, localName, qName, attributes);
        LOG.trace("{}{{ {}", indent, child);
        cache.put(e.getKey(), child);
        current = child;
        return;
      }
    }
    if (qName.equals(tag)) {
      throw new IllegalStateException("nested");
    }
    startTag(uri, localName, qName, attributes);
  }

  @Override
  public final void endElement(
    final String uri, final String localName, final String qName
  ) {
    if (current != null) {
      current.endElement(uri, localName, qName);
    } else if (qName.equals(tag)) {
      exit(uri, localName, qName);
      LOG.trace("{}}} {}", indent.substring(2), this);
      parent.current = null; // TODO stack?
    } else {
      endTag(uri, localName, qName);
    }
  }

  @Override
  public void characters(final char[] ch, final int start, final int length) throws SAXException {
    if (current != null) {
      current.characters(ch, start, length);
    }
  }

  public void init(
    final String uri, final String localName, final String qName, final Attributes attributes
  ) {
  }

  public void startTag(
    final String uri, final String localName, final String qName, final Attributes attributes
  ) {
    // System.out.println(indent + "start " + qName);
  }

  public void exit(
    final String uri, final String localName, final String qName
  ) {
  }

  public void endTag(
    final String uri, final String localName, final String qName
  ) {
    // System.out.println(indent + "end " + qName);
  }
}
