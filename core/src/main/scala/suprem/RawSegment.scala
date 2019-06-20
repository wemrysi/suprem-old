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

import scala.{Any, Array, Boolean, Byte}

import java.lang.SuppressWarnings
import java.util.Arrays

/** An unparsed chunk of input from a structured source.
  *
  * @param context semantic location of the segment in the source
  * @param format the source data format
  * @param data the bytes comprising the segment
  */
final case class RawSegment(
    context: StructurePath,
    format: SegmentFormat,
    data: Array[Byte]) {

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  override def equals(other: Any): Boolean =
    other match {
      case RawSegment(c, f, d) =>
        context == c && format == f && Arrays.equals(data, d)

      case _ => false
    }
}
