/*
 * Copyright © 2014 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

import javax.swing.JComboBox;
import java.util.Objects;

/**
 * A function that uses the currently selected font for all characters.
 */

public final class SigilFontFunctionSelected implements SigilFontFunctionType
{
  private final JComboBox<String> box;

  /**
   * Construct a font function.
   *
   * @param in_box The font selector.
   */

  public SigilFontFunctionSelected(
    final JComboBox<String> in_box)
  {
    this.box = Objects.requireNonNull(in_box, "Box");
  }

  @SuppressWarnings("boxing")
  @Override
  public String getFont(
    final Character c,
    final int size)
  {
    final String r = String.format("%s %d", this.box.getSelectedItem(), size);
    assert r != null;
    return r;
  }

  @Override
  public String toString()
  {
    return "Use selected font";
  }
}
