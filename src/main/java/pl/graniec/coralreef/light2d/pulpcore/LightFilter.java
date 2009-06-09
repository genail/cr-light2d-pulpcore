/**
 * Copyright (c) 2009, Coral Reef Project
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of the Coral Reef Project nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package pl.graniec.coralreef.light2d.pulpcore;

import java.util.Arrays;

import pl.graniec.coralreef.geometry.Geometry;
import pl.graniec.coralreef.geometry.Point2;
import pulpcore.image.CoreImage;
import pulpcore.image.filter.Filter;

/**
 * @author Piotr Korzuszek <piotr.korzuszek@gmail.com>
 *
 */
public class LightFilter extends Filter {

	private static class Node {
		int x, y;

		public Node(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
		
	}
	
	/** Geometry of the light */
	private Geometry lightGeometry;
	/** Center point of the light */
	private int centerX, centerY;
	
	/**
	 * 
	 */
	public LightFilter() {
	}
	
	/* (non-Javadoc)
	 * @see pulpcore.image.filter.Filter#copy()
	 */
	@Override
    public Filter copy() {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see pulpcore.image.filter.Filter#filter(pulpcore.image.CoreImage, pulpcore.image.CoreImage)
     */
    @Override
    protected void filter(CoreImage input, CoreImage output) {
        
        if (lightGeometry == null) {
            return;
        }
        
        final int[] srcPixels = input.getData();
        final int[] dstPixels = output.getData();
        
        // make all pixels invisible
        Arrays.fill(dstPixels, 0);
        
        // draw geometry lines
        Point2 first = null;
        Point2 prev, next = null;
        
        for (Point2 p : lightGeometry.getVerticles()) {
        	if (first == null) {
        		first = p;
        	}
        	
        	prev = next;
        	next = p;
        	
        	if (prev != null) {
        		line((int) prev.x, (int) prev.y, (int) next.x, (int) next.y, output);
        	}
        }
        
        if (next != null && first != next) {
        	line((int) first.x, (int) first.y, (int) next.x, (int) next.y, output);
        }
        
        
    }

    public void setLightGeometry(Geometry lightGeometry, int centerX, int centerY) {
        this.lightGeometry = lightGeometry;
        this.centerX = centerX;
        this.centerY = centerY;
    }
    
    private void line(int x0, int y0, int x1, int y1, CoreImage out) {
        int tmp;
        final int[] pixels = out.getData();
        final int imageWidth = out.getWidth();
        
        int Dx = x1 - x0; 
        int Dy = y1 - y0;
        boolean steep = (Math.abs(Dy) >= Math.abs(Dx));
        if (steep) {
            //SWAP(x0, y0);
            tmp = x0;
            x0 = y0;
            y0 = tmp;
           
            //SWAP(x1, y1);
            tmp = x1;
            x1 = y1;
            y1 = tmp;
           
            // recompute Dx, Dy after swap
            Dx = x1 - x0;
            Dy = y1 - y0;
        }
        
        int xstep = 1;
       
        if (Dx < 0) {
            xstep = -1;
            Dx = -Dx;
        }
        int ystep = 1;
        if (Dy < 0) {
            ystep = -1;        
            Dy = -Dy; 
        }
        int TwoDy = 2*Dy; 
        int TwoDyTwoDx = TwoDy - 2*Dx; // 2*Dy - 2*Dx
        int E = TwoDy - Dx; //2*Dy - Dx
        int y = y0;
        int xDraw, yDraw;    
        for (int x = x0; x != x1; x += xstep) {        
            if (steep) {            
                xDraw = y;
                yDraw = x;
            } else {            
                xDraw = x;
                yDraw = y;
            }
            // plot
            //plot(xDraw, yDraw);
            pixels[yDraw * imageWidth + xDraw] = 0xFFFFFFFF;
            // next
            if (E > 0) {
                E += TwoDyTwoDx; //E += 2*Dy - 2*Dx;
                y = y + ystep;
            } else {
                E += TwoDy; //E += 2*Dy;
            }
        }
    }

}
