package com.deedm.legacy.graphicAnimation;

import java.util.*;
import java.awt.*;

/**
 * 用于计算一个视图(AnimationView)的动画路径，所谓动画路径就是指一系列的坐标位置，视图根据该位置
 * 进行移动，从而形成动画
 * @author 周晓聪
 *
 */
public class AnimationPath {
	protected ArrayList<Point> path;
	
	public AnimationPath() { 
		path = new ArrayList<Point>(20);
	}
	
	/**
	 * 加入动画路径的起点
	 */
	public void addStartPoint(Point point) {
		path.clear();
		path.add(point);
	}
	
	public void add(Point point) {  path.add(point); }

	/**
	 * 清除原有的路径
	 */
	public void clear() { 	path.clear();  }

	/**
	 * 返回路径长度
	 */
	public int size() {  return path.size();  }

	/**
	 * 返回路径上某位置上的点坐标
	 * @param index 位置下标
	 * @return 该位置处的坐标
	 */
	public Point get(int index) { return path.get(index);  }
	
	/**
	 * 从最后加入的点再往上移动 steps ，一次移动 unit 个点。steps 和 unit 都必须为正数！
	 */
	public void moveUp(int steps, int unit) {
		// steps 和 unit 都只能是正数
		if (steps <= 0 || unit < 0) return;
		if (unit == 0) unit = steps; // 如果unit是零，则一步到位
		// 如果不存在最后加入的点，则不能实现移动
		int size = path.size();
		if (size <= 0) return;
		Point start = path.get(size-1); // 取最后加入点的坐标
		int x = start.x;
		int y = start.y;
		int hasMoved = 0;
		
		while (hasMoved + unit < steps) {
			y = y - unit;
			path.add(new Point(x, y));
			hasMoved = hasMoved + unit;
		}
		// 计算最后一次应该移动到的点，因为 steps 不一定是 unit 的整数倍，所以最后一次的
		// 移动需要特别计算
		y = y - (steps - hasMoved);
		path.add(new Point(x, y));
	}

	/**
	 * 从最后加入的点再往下移动 steps ，一次移动 unit 个点。steps 和 unit 都必须为正数！
	 */
	public void moveDown(int steps, int unit) {
		// steps 和 unit 都只能是正数
		if (steps <= 0 || unit < 0) return;	
		if (unit == 0) unit = steps; // 如果unit是零，则一步到位
		// 如果不存在最后加入的点，则不能实现移动
		int size = path.size();
		if (size <= 0) return;
		Point start = path.get(size-1); // 取最后加入点的坐标
		int x = start.x;
		int y = start.y;
		int hasMoved = 0;
		
		while (hasMoved + unit < steps) {
			y = y + unit;
			path.add(new Point(x, y));
			hasMoved = hasMoved + unit;
		}
		// 计算最后一次应该移动到的点，因为 steps 不一定是 unit 的整数倍，所以最后一次的
		// 移动需要特别计算
		y = y + (steps - hasMoved);
		path.add(new Point(x, y));
	}

	/**
	 * 从最后加入的点再往左移动 steps ，一次移动 unit 个点。steps 和 unit 都必须为正数！
	 */
	public void moveLeft(int steps, int unit) {
		// steps 和 unit 都只能是正数
		if (steps <= 0 || unit < 0) return;	
		if (unit == 0) unit = steps; // 如果unit是零，则一步到位
		// 如果不存在最后加入的点，则不能实现移动
		int size = path.size();
		if (size <= 0) return;
		Point start = path.get(size-1); // 取最后加入点的坐标
		int x = start.x;
		int y = start.y;
		int hasMoved = 0;
		
		while (hasMoved + unit < steps) {
			x = x - unit;
			path.add(new Point(x, y));
			hasMoved = hasMoved + unit;
		}
		// 计算最后一次应该移动到的点，因为 steps 不一定是 unit 的整数倍，所以最后一次的
		// 移动需要特别计算
		x = x - (steps - hasMoved);
		path.add(new Point(x, y));
	}

	/**
	 * 从最后加入的点再往右移动 steps ，一次移动 unit 个点。steps 和 unit 都必须为正数！
	 */
	public void moveRight(int steps, int unit) {
		// steps 和 unit 都只能是正数
		if (steps <= 0 || unit < 0) return;	
		if (unit == 0) unit = steps; // 如果unit是零，则一步到位
		// 如果不存在最后加入的点，则不能实现移动
		int size = path.size();
		if (size <= 0) return;
		Point start = path.get(size-1); // 取最后加入点的坐标
		int x = start.x;
		int y = start.y;
		int hasMoved = 0;
		
		while (hasMoved + unit < steps) {
			x = x + unit;
			path.add(new Point(x, y));
			hasMoved = hasMoved + unit;
		}
		// 计算最后一次应该移动到的点，因为 steps 不一定是 unit 的整数倍，所以最后一次的
		// 移动需要特别计算
		x = x + (steps - hasMoved);
		path.add(new Point(x, y));
	}

	/**
	 * 生成从当前路径的最后一点志移动到某一点的动画路径
	 */
	public void moveTo(Point end, int unit) {
		if (unit <= 0) {
			path.add(end);
			return;
		}
		
		int size = path.size();
		if (size <= 0) return;
		Point start = path.get(size-1);
		// 计算直线的斜率
		double k = ((double)(end.y-start.y))/((double)(end.x-start.x));
		
		int x = start.x;
		int y = start.y;
		int xDistance = end.x - start.x;
		int yDistance = end.y - start.y;
		if (xDistance == 0 && yDistance == 0) return;		// end 与 start 相等
		
		int distance;
		int fact = 1;
		int hasMoved = 0;

		// 取 x 方向距离和 y 方向距离大者计算坐标的单位改变值，这样会使得动画更为平稳，当然更正确的方法
		// 是根据直线本身的方向计算坐标的单位改变值，但是这样的计算比较复杂，目前暂时使用简单计算方法
		if (Math.abs(xDistance) > Math.abs(yDistance)) {
			// 取 x 方向的距离计算坐标单位改变值，注意这时 xDistance 不可能为0
			if (xDistance < 0) { distance = -xDistance; fact = -1; }  
			else distance = xDistance;

			// 使用 unit 改变 x ，同时通过直线的斜率得到相应的 y 坐标
			while (hasMoved + unit < distance) {
				x = x + fact * unit;
				// 根据直线方程计算 y 坐标值
				y = (int)(k*(x-start.x) + start.y);
				path.add(new Point(x, y));
				hasMoved = hasMoved + unit;
			}
			// 最后直接加入终点位置
			path.add(end);
		} else {
			// 取 y 方向的距离计算坐标单位改变值，注意这时 yDistance 不可能为0
			if (yDistance < 0) { distance = -yDistance; fact = -1; }
			else distance = yDistance;

			// 使用 unit 改变 y ，同时通过直线斜率（的倒数）得到相应的 x 坐标
			k = 1/k;
			while (hasMoved + unit < distance) {
				y = y + fact * unit;
				x = (int)(k* (y-start.y) + start.x); 
				path.add(new Point(x, y));
				hasMoved = hasMoved + unit;
			}
			// 最后直接加入终点位置
			path.add(end);
		}
	}

	
	/**
	 * 其他复杂路径的生成留待以后实现！
	 * 
	 */

	public void println() {
		for (int i = 0; i < path.size(); i++) {
			System.out.print("(" + path.get(i).x + ", " + path.get(i).y + ") ");
		}
		System.out.println();
	}
}
