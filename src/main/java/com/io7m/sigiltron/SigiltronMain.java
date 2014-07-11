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

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.io7m.jlog.Log;
import com.io7m.jlog.LogLevel;
import com.io7m.jlog.LogPolicyAllOn;
import com.io7m.jlog.LogType;

/**
 * Main program.
 */

public final class SigiltronMain
{
  /**
   * Main function.
   *
   * @param args
   *          Command line arguments.
   */

  public static void main(
    final String[] args)
  {
    SwingUtilities.invokeLater(new Runnable() {
      @SuppressWarnings("unused") @Override public void run()
      {
        new SigiltronMain();
      }
    });
  }

  private SigiltronMain()
  {
    final LogType log =
      Log.newLog(LogPolicyAllOn.newPolicy(LogLevel.LOG_DEBUG), "sigiltron");

    final SigiltronMainWindow win = new SigiltronMainWindow(log);
    // win.setPreferredSize(new Dimension(640, 480));
    win.setTitle("Sigiltron");
    win.pack();
    win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    win.setVisible(true);
  }
}