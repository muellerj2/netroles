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

package ch.ethz.sn.visone3.progress;

/**
 * Reports progress updates.
 *
 */
public interface ProgressSource extends AutoCloseable {
  /**
   * Progress source that does nothing.
   */
  ProgressSource NULL = new ProgressSource() {

    @Override
    public void updateProgress(final int last, final int expected, final String message) {
    }

    @Override
    public String getMessage() {
      return "";
    }

    @Override
    public long getTimeStampCreate() {
      return -1;
    }

    @Override
    public long getTimeStampDie() {
      return -1;
    }

    @Override
    public int getLast() {
      return -1;
    }

    @Override
    public int getExpected() {
      return -1;
    }

    @Override
    public void close() {
    }
  };

  int UNKNOWN = -1;

  /**
   * Reports on recent progress.
   * 
   * @param last
   *          latest progress score.
   */
  default void updateProgress(final int last) {
    updateProgress(last, UNKNOWN);
  }

  /**
   * Reports on recent progress.
   * 
   * @param last
   *          latest progress score.
   * @param expected
   *          expected total progress score.
   */
  default void updateProgress(final int last, final int expected) {
    updateProgress(last, expected, getMessage());
  }

  /**
   * Reports on recent progress in the form of a message.
   * 
   * @param message
   *          message on recent progress.
   */
  default void updateProgress(final String message) {
    updateProgress(getLast(), getExpected(), message);
  }

  /**
   * Reports on recent progress.
   * 
   * @param last
   *          latest progress score.
   * @param expected
   *          expected total progress score.
   * @param message
   *          message on recent progress.
   */
  void updateProgress(int last, int expected, String message);

  /**
   * Increases the latest progress score.
   */
  default void increaseProgress() {
    updateProgress(getLast() + 1, getExpected());
  }

  /**
   * Returns the latest message on progress.
   * 
   * @return the message.
   */
  String getMessage();

  /**
   * Returns the time stamp when this source was created.
   * 
   * @return the creation time stamp
   */
  long getTimeStampCreate();

  /**
   * Returns the time stamp when this source was closed.
   * 
   * @return the closing time stamp
   */
  long getTimeStampDie();

  /**
   * Returns the latest reported progress score.
   * 
   * @return the latest progress score.
   */
  int getLast();

  /**
   * Returns the expected total progress score.
   * 
   * @return the expected total progress score.
   */
  int getExpected();

  /**
   * Closes the progress source, reporting the underlying process as finished (whether successfully
   * or not).
   */
  @Override
  void close();
}
