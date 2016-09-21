package diskworld.extension;

public class DiskworldVector {
    public int x, y;

    public DiskworldVector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void stretch(double factor) {
        this.x *= factor;
        this.y *= factor;
    }

    public void add(DiskworldVector other) {
        this.x += other.x;
        this.y += other.y;
    }

    public static DiskworldVector add(DiskworldVector v1, DiskworldVector v2) {
        return new DiskworldVector(v1.x + v2.x, v1.y + v2.y);
    }

    public void print() {
        System.out.println("Vector (" + this.x + " | " + this.y + ")");
    }

    public static void main(String[] args) {
        DiskworldVector nullVector = new DiskworldVector(0, 0);
        DiskworldVector einsZwei = new DiskworldVector(1, 2);
        einsZwei.add(nullVector);
        einsZwei.print();
        einsZwei.stretch(2);
        einsZwei.print();
        DiskworldVector achtSechs = new DiskworldVector(8, 6);
        einsZwei.add(achtSechs);
        einsZwei.print();
    }

}
