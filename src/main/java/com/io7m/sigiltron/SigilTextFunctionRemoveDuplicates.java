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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A function that removes duplicate characters.
 */

public final class SigilTextFunctionRemoveDuplicates implements
SigilTextFunctionType
{
  /**
   * Construct a function.
   */

  public SigilTextFunctionRemoveDuplicates()
  {

  }

  @Override public List<Character> process(
    final List<Character> cs)
    {
    final Set<Character> m = new HashSet<Character>();
    final List<Character> xs = new ArrayList<Character>();

    for (int index = 0; index < cs.size(); ++index) {
      final Character c = cs.get(index);
      if (m.contains(c) == false) {
        xs.add(c);
      }
      m.add(c);
    }

    return xs;
    }

  @Override public String toString()
  {
    return "Remove duplicates";
  }
}
