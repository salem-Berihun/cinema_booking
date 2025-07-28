public class SeatDAO {

    public static Seat getSeatFromRowCol(int row, int col) {
        // Not stored separately yet. You can return simple new Seat:
        return new Seat(row, col);
    }
}
