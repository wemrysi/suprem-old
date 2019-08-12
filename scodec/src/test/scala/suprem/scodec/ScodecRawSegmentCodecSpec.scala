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

package suprem.scodec

import suprem.{RawSegment, RawSegmentCodecSpec}

import scodec.bits.BitVector

object ScodecRawSegmentCodecSpec extends RawSegmentCodecSpec {
  def encode(seg: RawSegment) =
    rawSegmentCodec.encode(seg).require

  def decode(bits: BitVector) =
    rawSegmentCodec.decode(bits).require.value
}
