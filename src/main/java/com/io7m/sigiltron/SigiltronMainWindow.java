/*
 * Copyright © 2014 <code@io7m.com> http://io7m.com
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.sigiltron;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.java.dev.designgridlayout.DesignGridLayout;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.swing.JSVGCanvas;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

@SuppressWarnings({ "boxing", "synthetic-access" }) final class SigiltronMainWindow extends
  JFrame
{
  private static final long serialVersionUID = 4203471970752993711L;

  private static List<String> getFontList()
  {
    final GraphicsEnvironment ge =
      GraphicsEnvironment.getLocalGraphicsEnvironment();

    final String[] fontnames = ge.getAvailableFontFamilyNames();
    final List<String> rs = new ArrayList<String>();

    for (final String name : fontnames) {
      assert name != null;

      if (name.contains("'")) {
        continue;
      }

      rs.add(name);
    }

    return rs;
  }

  private final JSVGCanvas                           canvas;
  private JPanel                                     controls;
  private @Nullable SVGDocument                      document;
  private final WeakHashMap<String, Font>            font_cache;
  private final JComboBox<SigilFontFunctionType>     font_function;
  private final JComboBox<String>                    fonts;
  private final JComboBox<SigilTextFunctionType>     function;
  private final LogUsableType                        rlog;
  private final JComboBox<SigilRotationFunctionType> rotation_function;
  private final JButton                              save;
  private final JFormattedTextField                  spread;

  private JComboBox<SigilSpreadFunctionType>         spread_function;

  SigiltronMainWindow(
    final LogUsableType in_log)
  {
    final Container c = this.getContentPane();

    this.rlog = NullCheck.notNull(in_log, "Log");
    this.document = null;

    this.font_cache = new WeakHashMap<String, Font>();

    this.canvas = new JSVGCanvas();
    this.canvas.setPreferredSize(new Dimension(640, 480));
    this.canvas.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

    {
      this.function = new JComboBox<SigilTextFunctionType>();
      this.function.addItem(new SigilTextFunctionRemoveDuplicates());
      final SigilTextFunctionIdentity default_item =
        new SigilTextFunctionIdentity();
      this.function.addItem(default_item);
      this.function.setSelectedItem(default_item);
    }

    this.fonts = new JComboBox<String>();
    for (final String f : SigiltronMainWindow.getFontList()) {
      this.fonts.addItem(f);
    }

    {
      this.font_function = new JComboBox<SigilFontFunctionType>();
      this.font_function.addItem(new SigilFontFunctionSelected(this.fonts));
      final SigilFontFunctionRandom default_item =
        new SigilFontFunctionRandom(this.fonts);
      this.font_function.addItem(default_item);
      this.font_function.setSelectedItem(default_item);
    }

    {
      this.rotation_function = new JComboBox<SigilRotationFunctionType>();
      this.rotation_function.addItem(new SigilRotationFunctionRandom());
      final SigilRotationFunctionRandom45 default_item =
        new SigilRotationFunctionRandom45();
      this.rotation_function.addItem(default_item);
      this.rotation_function.setSelectedItem(default_item);
    }

    this.spread = new JFormattedTextField();
    this.spread.setValue(Integer.valueOf(250));

    {
      this.spread_function = new JComboBox<SigilSpreadFunctionType>();
      this.spread_function.addItem(new SigilSpreadFunctionExact());
      final SigilSpreadFunctionRandom default_item =
        new SigilSpreadFunctionRandom();
      this.spread_function.addItem(default_item);
      this.spread_function.setSelectedItem(default_item);
    }

    this.save = new JButton("Save...");
    this.save.setEnabled(false);
    this.save.addActionListener(new ActionListener() {
      @Override public void actionPerformed(
        @Nullable final ActionEvent e)
      {
        try {
          assert SigiltronMainWindow.this.document != null;

          final JFileChooser dialog = new JFileChooser();
          dialog.setMultiSelectionEnabled(false);

          final int r = dialog.showSaveDialog(c);
          if (r == JFileChooser.APPROVE_OPTION) {
            final File f = dialog.getSelectedFile();

            final SVGGraphics2D g =
              new SVGGraphics2D(SigiltronMainWindow.this.document);
            g.stream(f.toString());
          }
        } catch (final HeadlessException x) {
          SigilErrorBox.showError(SigiltronMainWindow.this.rlog, x);
        } catch (final SVGGraphics2DIOException x) {
          SigilErrorBox.showError(SigiltronMainWindow.this.rlog, x);
        }
      }
    });

    final JTextField input = new JTextField();
    final JButton input_now = new JButton("Generate");
    input_now.addActionListener(new ActionListener() {
      @Override public void actionPerformed(
        @Nullable final ActionEvent e)
      {
        final List<Character> cs = new ArrayList<Character>();
        final String text = input.getText();
        for (int index = 0; index < text.length(); ++index) {
          final char ch = text.charAt(index);
          cs.add(new Character(ch));
        }

        final SigiltronMainWindow sw = SigiltronMainWindow.this;
        final SigilTextFunctionType f =
          (SigilTextFunctionType) sw.function.getSelectedItem();
        final SigilFontFunctionType ff =
          (SigilFontFunctionType) sw.font_function.getSelectedItem();
        final SigilRotationFunctionType rf =
          (SigilRotationFunctionType) sw.rotation_function.getSelectedItem();
        final SigilSpreadFunctionType sf =
          (SigilSpreadFunctionType) sw.spread_function.getSelectedItem();

        assert ff != null;
        assert rf != null;
        assert sf != null;

        sw.generateImage(f.process(cs), ff, rf, sf);
        sw.save.setEnabled(true);
      }
    });

    {
      this.controls = new JPanel();
      final DesignGridLayout dg = new DesignGridLayout(this.controls);
      dg.row().grid(new JLabel("Text function")).add(this.function);
      dg.row().grid(new JLabel("Font function")).add(this.font_function);
      dg
        .row()
        .grid(new JLabel("Rotation function"))
        .add(this.rotation_function);
      dg.row().grid(new JLabel("Font")).add(this.fonts);
      dg.row().grid(new JLabel("Spread")).add(this.spread);
      dg.row().grid(new JLabel("Spread function")).add(this.spread_function);
      dg.row().grid(new JLabel("Intent")).add(input, 3).add(input_now);
      dg.row().grid().add(this.save);
    }

    final DesignGridLayout dg = new DesignGridLayout(c);
    dg.row().grid().add(this.canvas);
    dg.row().grid().add(this.controls);

  }

  private void generateImage(
    final List<Character> cs,
    final SigilFontFunctionType ff,
    final SigilRotationFunctionType rf,
    final SigilSpreadFunctionType sf)
  {
    final DOMImplementation impl =
      SVGDOMImplementation.getDOMImplementation();
    final String svg_ns = SVGDOMImplementation.SVG_NAMESPACE_URI;
    final SVGDocument doc =
      (SVGDocument) impl.createDocument(svg_ns, "svg", null);

    // Create a converter for this document.
    final SVGGraphics2D g = new SVGGraphics2D(doc);

    final int center_x = this.canvas.getWidth() / 2;
    final int center_y = this.canvas.getHeight() / 2;

    g.transform(new AffineTransform());
    g.translate(center_x, center_y);

    for (final Character c : cs) {
      assert c != null;

      final int font_size = (int) ((Math.random() * 100) + 100);
      final String font_name = ff.getFont(c, font_size);

      final Font font;
      if (this.font_cache.containsKey(font_name)) {
        font = this.font_cache.get(font_name);
      } else {
        font = Font.decode(font_name);
      }
      this.font_cache.put(font_name, font);

      g.setFont(font);
      g.rotate(rf.getRotation(c));
      g.drawString("" + c, 0, sf.getSpread((Integer) this.spread.getValue()));
    }

    final Element root = doc.getDocumentElement();
    g.getRoot(root);

    this.canvas.setSVGDocument(doc);
    this.document = doc;
  }
}
