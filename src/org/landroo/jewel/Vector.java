package org.landroo.jewel;

import android.util.FloatMath;

public class Vector 
{
	public static float TO_RADIANS = (1 / 180.0f) * (float) Math.PI;
    public static float TO_DEGREES = (1 / (float) Math.PI) * 180;
    public float x, y;

    public Vector() 
    {
    } 

    public Vector(float x, float y) 
    {
        this.x = x;
        this.y = y;
    }
    
    public Vector(Vector other) 
    {
        this.x = other.x;
        this.y = other.y; 
    } 
    
    public Vector cpy() 
    {
        return new Vector(x, y); 
    }
    
    public Vector set(float x, float y) 
    {
        this.x = x;
        this.y = y;
        return this; 
    }
 
    public Vector set(Vector other) 
    {
        this.x = other.x;
        this.y = other.y;
        return this; 
    }
    
    public Vector add(float x, float y) 
    {
        this.x += x;
        this.y += y;
        return this; 
    }

    public Vector add(Vector other) 
    { 
        this.x += other.x;
        this.y += other.y;
        return this;
    } 

    public Vector sub(float x, float y) 
    {
        this.x -= x; 
        this.y -= y;
        return this; 
    }
 
    public Vector sub(Vector other) 
    {
        this.x -= other.x;
        this.y -= other.y;
        return this; 
    } 
    
    public Vector mul(float scalar) 
    {
        this.x *= scalar;
        this.y *= scalar;
        return this; 
    }
    
    public float len() 
    {
        return FloatMath.sqrt(x * x + y * y); 
    } 
    
    public Vector nor() 
    {
        float len = len();
        if (len != 0) 
        {
            this.x /= len;
            this.y /= len; 
        }
        return this;
    } 
    
    public float angle() 
    {
        float angle = (float) Math.atan2(y, x) * TO_DEGREES;
        if (angle < 0) 
            angle += 360;
        return angle;
    } 
    
    public Vector rotate(float angle) 
    { 
    	float rad = angle * TO_RADIANS;
        float cos = FloatMath.cos(rad);
        float sin = FloatMath.sin(rad); 

        float newX = this.x * cos - this.y * sin;
        float newY = this.x * sin + this.y * cos; 

        this.x = newX;
        this.y = newY; 

        return this; 
    }
    
    public float dist(Vector other) 
    {
        float distX = this.x - other.x;
        float distY = this.y - other.y;
        return FloatMath.sqrt(distX * distX + distY * distY); 
    }
 
    public float dist(float x, float y) 
    {
        float distX = this.x - x;
        float distY = this.y - y;
        return FloatMath.sqrt(distX * distX + distY * distY); 
    }

    public float viewAng(float x, float y)
    {
    	return viewAng(new Vector(x, y));
    }
    
    public float viewAng(Vector other)
    {
		float nDelX = other.x - x;
		float nDelY = other.y - y;
		float nDe = 0;

		if (nDelX != 0)
		{
			nDe = 2 * (float)Math.PI;
			nDe = nDe + (float)Math.atan(nDelY / nDelX);
			if (nDelX <= 0)
			{
				nDe = (float)Math.PI;
				nDe = nDe + (float)Math.atan(nDelY / nDelX);
			}
			else
			{
				if (nDelY >= 0)
				{
					nDe = 0;
					nDe = nDe + (float)Math.atan(nDelY / nDelX);
				}
			}
		}
		else
		{
			if (nDelY == 0)
			{
				nDe = 0;
			}
			else
			{
				if (nDelY < 0)
				{
					nDe = (float)Math.PI;
				}
				nDe = nDe + (float)Math.PI / 2;
			}
		}
	
    	return nDe / (float)Math.PI * 180;
    }
}
