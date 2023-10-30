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
 * along with netroles.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * This package provides access to IO services for reading and writing networks.
 * 
 * <p>
 * Registered services for network formats can be accessed using the
 * {@link ch.ethz.sn.visone3.io.IoProvider} class. Services (implementing
 * {@link ch.ethz.sn.visone3.io.IoService}) provide methods to instantiate
 * instances of {@link ch.ethz.sn.visone3.io.Source} and
 * {@link ch.ethz.sn.visone3.io.Sink} to configure and perform the actual network input or output.
 * 
 * <p>
 * A typical code sequence to read network files looks like this:
 * 
 * <pre>
 * SourceFormat ioResult;
 * try (Source&lt;?&gt; source = IoProvider.getService(format).newSource(streamOrFile)) {
 *   // configure source by calling suitable methods
 *   // e.g., define the type and value range of a link attribute
 *   // (if it is not already specified in the input)
 *   source.linkrange("link attribute", valueRange);
 *   ioResult = source.parse();
 * }
 * // access results of parsing the input, e.g.,
 * Network network = ioResult.incidence(); // get the read network
 * // or  
 * </pre>
 * 
 * <p>
 * Similarly, code to write network files can take the following form:
 * 
 * <pre>
 * try (Sink sink = IoProvider.getService(format).newSink(outputStream)) {
 *   // configure sink, such as:
 *   sink.incidence(network); // choose network to output
 *   sink.link("example link attribute", linkAttribute); // add a link attribute
 *   sink.node("example node attribute", nodeAttribute); // or a node attribute 
 *   // output is completed when the sink is closed
 * }
 * </pre>
 * 
 */
package ch.ethz.sn.visone3.io;
