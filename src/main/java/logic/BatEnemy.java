package logic;

public class BatEnemy extends Enemy {
    public BatEnemy() {
        super(50, 2.5, 15, true);
    }

    @Override
    public void move(Waypoint target) {
        double dx = target.getX() - this.x;
        double dy = target.getY() - this.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            double moveX = (dx / distance) * speed;
            double moveY = (dy / distance) * speed;

            // Prevent overshooting
            if (Math.abs(moveX) > Math.abs(dx))
                this.x = target.getX();
            else
                this.x += moveX;

            if (Math.abs(moveY) > Math.abs(dy))
                this.y = target.getY();
            else
                this.y += moveY;
        }
    }
}
