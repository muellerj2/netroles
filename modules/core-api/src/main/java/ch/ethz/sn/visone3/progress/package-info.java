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
 * Classes for reporting operation progress.
 * 
 * <p>
 * Register your own listener on operation progress using:
 * 
 * <pre>
 * {@code
 * ProgressProvider.getMonitor().addListener(listener);
 * }
 * </pre>
 * 
 * <p>
 * If no listener has been registered before the first operation reporting
 * progress, a default listener printing to the standard logger might be
 * installed.
 * 
 * <p>
 * Operations reporting their own progress typically perform the following
 * sequence of operations:
 * 
 * <pre>
 * try (ProgressSource progressSource = ProgressProvider.getMonitor().newSource()) {
 *   // repeatedly update progress by calling
 *   progressSource.updateProgress(currentprogress, totalprogress, message);
 * }
 * </pre>
 */
package ch.ethz.sn.visone3.progress;
