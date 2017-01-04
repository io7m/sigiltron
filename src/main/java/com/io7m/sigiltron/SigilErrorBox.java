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

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.junreachable.UnreachableCodeException;
import net.java.dev.designgridlayout.DesignGridLayout;
import org.slf4j.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Error message functions.
 */

final class SigilErrorBox
{
  private SigilErrorBox()
  {
    throw new UnreachableCodeException();
  }

  private static JDialog showActualErrorBox(
    final String title,
    final String message,
    final @Nullable JTextArea backtrace)
  {
    final JDialog d = new JDialog();
    d.setTitle(title);
    d.setMinimumSize(new Dimension(320, 0));

    final JButton ok = new JButton("OK");
    ok.addActionListener(e -> SigilWindowUtilities.closeDialog(d));

    JLabel icon = null;
    try {
      icon = SigilIcons.makeErrorIcon();
    } catch (final IOException ex) {
      // Who cares?
    }

    final int max_length = 120;
    final String truncated;
    if (message.length() > max_length) {
      truncated = message.substring(0, max_length - 1) + "...";
    } else {
      truncated = message;
    }

    final JPanel main = new JPanel();
    final DesignGridLayout dg = new DesignGridLayout(main);
    dg.row().grid(icon).add(new JLabel(title));
    dg.emptyRow();
    dg.row().grid().add(new JLabel(truncated));

    if (backtrace != null) {
      final JScrollPane pane = new JScrollPane(backtrace);
      pane.setPreferredSize(new Dimension(600, 320));
      backtrace.setCaretPosition(0);

      final JLabel backtrace_note =
        new JLabel("The full error backtrace is as follows:");
      dg.emptyRow();
      dg.row().grid().add(backtrace_note);
      dg.emptyRow();
      dg.row().grid().add(pane);
    }

    dg.emptyRow();
    dg.row().grid().add(ok);

    d.setContentPane(main);
    d.pack();
    d.setVisible(true);
    return d;
  }

  private static JDialog showActualErrorWithException(
    final String title,
    final String message,
    final Throwable e)
  {
    e.printStackTrace();
    final JTextArea text = new JTextArea();
    text.setEditable(false);
    text.setText(showStackTraceText(e));

    return showActualErrorBox(title, message, text);
  }

  @SuppressWarnings("null")
  public static JDialog showError(
    final Logger log,
    final Throwable e)
  {
    final String title = e.getClass().getCanonicalName();
    log.error(showStackTraceText(e));
    return showActualErrorWithException(
      title,
      e.getMessage(),
      e);
  }

  public static void showErrorLater(
    final Logger log,
    final Throwable e)
  {
    SwingUtilities.invokeLater(() -> showError(log, e));
  }

  public static JDialog showErrorWithoutException(
    final Logger log,
    final String title,
    final String message)
  {
    log.error(title + ": " + message);
    return showActualErrorBox(title, message, null);
  }

  public static void showErrorWithoutExceptionLater(
    final Logger log,
    final String title,
    final String message)
  {
    log.error(title + ": " + message);

    SwingUtilities.invokeLater(() -> {
      final JTextArea text = new JTextArea();
      text.setEditable(false);
      text.setText(message);
      showActualErrorBox(title, message, text);
    });
  }

  @SuppressWarnings("null")
  public static JDialog showErrorWithTitle(
    final Logger log,
    final String title,
    final Throwable e)
  {
    log.error(showStackTraceText(e));
    return showActualErrorWithException(
      title,
      e.getMessage(),
      e);
  }

  public static void showErrorWithTitleLater(
    final Logger log,
    final String title,
    final Throwable e)
  {
    SwingUtilities.invokeLater(() -> showErrorWithTitle(log, title, e));
  }

  private static String showStackTraceText(
    final Throwable e)
  {
    try (final StringWriter writer = new StringWriter()) {
      writer.append(e.getMessage());
      writer.append(System.lineSeparator());
      writer.append(System.lineSeparator());

      e.printStackTrace(new PrintWriter(writer));
      return NullCheck.notNull(writer.toString());
    } catch (final IOException e1) {
      throw new UnreachableCodeException(e1);
    }
  }
}
