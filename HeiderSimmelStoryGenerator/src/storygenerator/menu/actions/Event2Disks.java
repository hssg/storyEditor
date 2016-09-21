package storygenerator.menu.actions;

import diskworld.Disk;
import diskworld.Environment;
import diskworld.linalg2D.Point;

/**
 * For Events which need a reference disk.
 *
 * @author Svenja
 */
public abstract class Event2Disks extends Event {
    private transient Disk referenceDisk;
    private String referenceDiskName;

    public String getReferenceDiskName() {
        return referenceDiskName;
    }

    public void setReferenceDiskName(String referenceDiskName) {
        this.referenceDiskName = referenceDiskName;
    }

    public Disk getReferenceDisk() {
        return referenceDisk;
    }

    public void setReferenceDisk(Disk referenceDisk) {
        this.referenceDisk = referenceDisk;
    }

    protected double[] avoid(Disk disk, Environment env) {
        double[] values;
        double targetx, targety;

        double distanceToBorders = 2 * disk.getRadius();
        distanceToBorders = distanceToBorders < 0.05 ? 0.05 : distanceToBorders;
        double minimalDistanceToOtherDisk = environmentSize / 3; //parametrization possible
        double minDistToChangeBehavior = (environmentSize + environmentSize) / 30;

        if (environmentSize < 2 * distanceToBorders || environmentSize < 2 * distanceToBorders) {
            System.out.println("Error: borders too small.");
        }

        Point enemy = new Point(getReferenceDisk().getX(), getReferenceDisk().getY());
        Point me = new Point(disk.getX(), disk.getY());

        double distToEnemy = me.distance(enemy);
        if (distToEnemy >= minimalDistanceToOtherDisk) {
            //			Point p = new Point(environmentX/2, environmentY/2);
            //			values = this.goToPoint(disk, p);
            values = this.stop(disk);
        } else {


			/* visualization: A, B, C and D...
             *
			  ___________________
			  |					 |
			  |		D--C		 |
			  |		|  |		 | <-Border
			  |		A--B..dist...|
			  |					 |
			  |__________________|
			 */

            Point A = new Point(distanceToBorders, distanceToBorders);
            Point B = new Point(environmentSize - distanceToBorders, distanceToBorders);
            Point C = new Point(environmentSize - distanceToBorders, environmentSize - distanceToBorders);
            Point D = new Point(distanceToBorders, environmentSize - distanceToBorders);


            // if you are in the corner: go to next corner(counterclockwise)
            /*
			if(distToEnemy < envx / 2) {
				if(this.corner.x == -1 && this.corner.y == -1) {
					double minDistToCorner = envx / 10; //param
					if(me.distance(A) < minDistToCorner)
						this.corner = B;
					else if(me.distance(B) < minDistToCorner)
						this.corner = C;
					else if(me.distance(C) < minDistToCorner)
						this.corner = D;
					else if(me.distance(D) < minDistToCorner)
						this.corner = A;
				} else {
					return goToPoint(disk, this.corner);
				}
			} else {
				this.corner = new Point(-1, -1);
			}
			 */

            Point iAB = Point.getIntersectionPoint(enemy, me, A, B);
            Point iBC = Point.getIntersectionPoint(enemy, me, B, C);
            Point iCD = Point.getIntersectionPoint(enemy, me, C, D);
            Point iDA = Point.getIntersectionPoint(enemy, me, D, A);

            Point possible1;
            Point possible2;

            boolean bAB = true;
            boolean bBC = true;
            boolean bCD = true;
            boolean bDA = true;

            if (enemy.y >= me.y)
                bCD = false;
            else
                bAB = false;
            if (enemy.x >= me.x)
                bBC = false;
            else
                bDA = false;

            if (bAB && bBC) {
                possible1 = iAB;
                possible2 = iBC;
            } else if (bAB && bCD) {
                possible1 = iAB;
                possible2 = iCD;
            } else if (bAB && bDA) {
                possible1 = iAB;
                possible2 = iDA;
            } else if (bBC && bCD) {
                possible1 = iBC;
                possible2 = iCD;
            } else if (bBC && bDA) {
                possible1 = iBC;
                possible2 = iDA;
            } else {
                if (!bCD || !bDA)
                    System.out.println("Error at Interactions.avoid");
                possible1 = iCD;
                possible2 = iDA;
            }

            //			System.out.println(bAB+" "+bBC+" "+bCD+" "+bDA);

            double distance1 = me.distance(possible1);
            double distance2 = me.distance(possible2);

            if (possible1.x < distanceToBorders || possible1.x > environmentSize - distanceToBorders || possible1.y < distanceToBorders || possible1.y > environmentSize - distanceToBorders) {
                targetx = possible2.x;
                targety = possible2.y;
            } else if (possible2.x < distanceToBorders || possible2.x > environmentSize - distanceToBorders || possible2.y < distanceToBorders || possible2.y > environmentSize - distanceToBorders) {
                targetx = possible1.x;
                targety = possible1.y;
            } else {
                if (distance1 < distance2) {
                    targetx = possible1.x;
                    targety = possible1.y;
                } else {
                    targetx = possible2.x;
                    targety = possible2.y;
                }
            }
            if (targetx < distanceToBorders || targety < distanceToBorders || targetx > environmentSize - distanceToBorders || targety > environmentSize - distanceToBorders) {
                return this.stop(disk);
            }

            values = this.goToPoint(disk, targetx, targety);
        }
        return values;
    }
}
