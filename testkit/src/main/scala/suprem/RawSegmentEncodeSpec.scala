/*
 * Copyright 2014–2019 SlamData Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package suprem

import scala.{Array, Int, List, StringContext}
import scala.util.{Left, Right}

import java.lang.SuppressWarnings

import org.specs2.mutable.Specification

import scodec.bits._

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
trait RawSegmentEncodeSpec extends Specification {

  def encode(seg: RawSegment): BitVector

  "encoding raw segment" should {
    "minimal JSON segment" >> {
      // [0x00000000 0x0 0x00000000]
      val seg = RawSegment(List(), SegmentFormat.Json, Array())

      encode(seg) must_=== BitVector.low(68)
    }

    "minimal CSV segment" >> {
      // [0x00000000 0x1 0x22225c 0x00000000]
      val seg = RawSegment(List(), SegmentFormat.Csv('"', '"', '\\'), Array())

      val len = BitVector.low(32)
      val tag = bin"0001"
      val tpe = hex"22225c".bits
      val data = BitVector.low(32)

      val exp = len ++ tag ++ tpe ++ data

      encode(seg) must_=== exp
    }

    "single index path" >> {
      // [0x00000001 0x0 0x00000005 0x0 0x00000000]
      val seg = RawSegment(List(Left(5)), SegmentFormat.Json, Array())

      val len = hex"00000001".bits
      val tag = bin"0000"
      val idx = hex"00000005".bits
      val tpe = bin"0000"
      val data = BitVector.low(32)

      val exp = len ++ tag ++ idx ++ tpe ++ data

      encode(seg) must_=== exp
    }

    "max index path" >> {
      // [0x00000001 0x0 0x7fffffff 0x0 0x00000000]
      val seg = RawSegment(List(Left(Int.MaxValue)), SegmentFormat.Json, Array())

      val len = hex"00000001".bits
      val tag = bin"0000"
      val idx = hex"7fffffff".bits
      val tpe = bin"0000"
      val data = BitVector.low(32)

      val exp = len ++ tag ++ idx ++ tpe ++ data

      encode(seg) must_=== exp
    }

    "min index path" >> {
      // [0x00000001 0x0 0x00000000 0x0 0x00000000]
      val seg = RawSegment(List(Left(0)), SegmentFormat.Json, Array())

      val len = hex"00000001".bits
      val tag = bin"0000"
      val idx = hex"00000000".bits
      val tpe = bin"0000"
      val data = BitVector.low(32)

      val exp = len ++ tag ++ idx ++ tpe ++ data

      encode(seg) must_=== exp
    }

    "single field path" >> {
      // [0x00000001 0x1 0x00000003 0x616161 0x0 0x00000000]
      val seg = RawSegment(List(Right("aaa")), SegmentFormat.Json, Array())

      val len = hex"00000001".bits
      val tag = bin"0001"
      val flen = hex"00000003".bits
      val field = hex"616161".bits
      val tpe = bin"0000"
      val data = BitVector.low(32)

      val exp = len ++ tag ++ flen ++ field ++ tpe ++ data

      encode(seg) must_=== exp
    }

    "empty field path" >> {
      // [0x00000001 0x1 0x00000000 0x0 0x00000000]
      val seg = RawSegment(List(Right("")), SegmentFormat.Json, Array())

      val len = hex"00000001".bits
      val tag = bin"0001"
      val flen = hex"00000000".bits
      val tpe = bin"0000"
      val data = BitVector.low(32)

      val exp = len ++ tag ++ flen ++ tpe ++ data

      encode(seg) must_=== exp
    }

    "multibyte field path" >> {
      // [0x00000001 0x1 0x0000000a 0x61.d091.e3819b.f0908f89 0x0 0x00000000]
      val seg = RawSegment(List(Right("aБせ𐏉")), SegmentFormat.Json, Array())

      val len = hex"00000001".bits
      val tag = bin"0001"
      val flen = hex"0000000a".bits
      val field = hex"61d091e3819bf0908f89".bits
      val tpe = bin"0000"
      val data = BitVector.low(32)

      val exp = len ++ tag ++ flen ++ field ++ tpe ++ data

      encode(seg) must_=== exp
    }

    "complex path" >> {
      // [0x00000004 0x1 0x00000002 0xd091 0x0 0x00000154 0x0 0x00000001 0x1 0x00000005 0x76616c7565 0x0 0x00000000]
      val seg = RawSegment(List(Right("Б"), Left(340), Left(1), Right("value")), SegmentFormat.Json, Array())

      val len = hex"00000004".bits
      val p0 = bin"0001" ++ hex"00000002".bits ++ hex"d091".bits
      val p1 = bin"0000" ++ hex"00000154".bits
      val p2 = bin"0000" ++ hex"00000001".bits
      val p3 = bin"0001" ++ hex"00000005".bits ++ hex"76616c7565".bits
      val tpe = bin"0000"
      val data = BitVector.low(32)

      val exp = len ++ p0 ++ p1 ++ p2 ++ p3 ++ tpe ++ data

      encode(seg) must_=== exp
    }

    "non-empty data" >> {
      val data = hex"8736da541181652419e1f1aa6cbb7b37".bits

      // [0x00000001 0x1 0x00000005 0x76616c7565 0x1 0x22225c 0x00000010 0x8736da541181652419e1f1aa6cbb7b37]
      val seg = RawSegment(List(Right("value")), SegmentFormat.Csv('"', '"', '\\'), data.toByteArray)

      val len = hex"00000001".bits
      val p0 = bin"0001" ++ hex"00000005".bits ++ hex"76616c7565".bits
      val tpe = bin"0001" ++ hex"22225c".bits
      val d = hex"00000010".bits ++ data

      val exp = len ++ p0 ++ tpe ++ d

      encode(seg) must_=== exp
    }
  }
}
