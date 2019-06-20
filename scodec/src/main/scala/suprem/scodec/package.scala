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

import scala.{Array, Byte}
import scala.util.{Left, Right}

import _root_.scodec.Codec
import _root_.scodec.bits._
import _root_.scodec.codecs._

package object scodec {
  val segmentFormatCodec: Codec[SegmentFormat] = {
    val csv = (byte :: byte :: byte).as[SegmentFormat.Csv]

    discriminated[SegmentFormat].by(uint4)
      .| (0) { case SegmentFormat.Json => () } (_ => SegmentFormat.Json) (ignore(0))
      .| (1) { case c @ SegmentFormat.Csv(_, _, _)  => c } (c => c) (csv)
  }

  val rawSegmentCodec: Codec[RawSegment] = {
    val nodeCodec: Codec[StructureNode] =
      discriminated[StructureNode].by(uint4)
        .| (0) { case Left(i) => i } (Left(_)) (int32)
        .| (1) { case Right(s) => s } (Right(_)) (utf8_32)

    val pathCodec: Codec[StructurePath] =
      listOfN(int32, nodeCodec)

    val dataCodec: Codec[Array[Byte]] =
      variableSizeBytes(int32, bits.xmap(_.toByteArray, BitVector.view))

    (pathCodec :: segmentFormatCodec :: dataCodec).as[RawSegment]
  }
}
