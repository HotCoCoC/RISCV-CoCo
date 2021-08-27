package mypack

import chisel3._
import chisel3.tester._
//import chiseltest._
import org.scalatest._
import scala.util._
import scala.util.Random
//which is package to make vcd 
import chiseltest.experimental.TestOptionBuilder._
import chiseltest.internal.WriteVcdAnnotation


class ANDSpec extends FreeSpec with ChiselScalatestTester with Matchers{
    "AND should " in {
        test(new AND) { c =>
            c.io.a.poke(0.U)     // Set our input to value 0
            c.io.b.poke(0.U)     // Set our input to value 0
            c.io.out.expect(0.U)  // Assert that the output correctly has 0

            c.io.a.poke(1.U)     // Set our input to value 1
            c.io.out.expect(0.U)  // Assert that the output correctly has 1
            c.io.b.poke(1.U)     // Set our input to value 2
            c.io.out.expect(1.U)  // Assert that the output correctly has 2
        }

    }
    
}

class RegfileSpec extends FreeSpec with ChiselScalatestTester with Matchers{
    "RegFile should be OK " in {
        test(new RegisterFile).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
            c.io.wen.poke(true.B)
            var x=0;
            for ( x <- 0 to 31){
                c.io.waddr.poke(x.U)
                c.io.wdata.poke((x+1).U)
                c.clock.step(10)
            }
            c.io.rs1_addr.poke(0.U)
            c.io.rs1_data.expect(0.U)
            c.clock.step(1)
            for ( x <- 1 to 31){
                c.io.rs1_addr.poke(x.U)
                c.io.rs1_data.expect((x+1).U)
                c.clock.step(10)
            } 
        }
    }
}

class FetchSpec extends FreeSpec with ChiselScalatestTester with Matchers{
    "Fetch should be OK " in {
        test(new FetchDataPath) { c =>
            //test reset function
            c.io.ice.expect(false.B)
            c.reset.poke(true.B)
            c.clock.step()
            c.io.ice.expect(false.B)
            c.reset.poke(false.B)
            c.clock.step()
            c.io.ice.expect(true.B)
            c.clock.step()
            c.reset.poke(true.B)
            c.io.ice.expect(true.B)
            c.clock.step()
            c.reset.poke(false.B)
            c.clock.step()
            //reset finish
            var x=0
            for(x <- 0 to 100){
                c.io.ice.expect(true.B)
                c.io.pc.expect((4*x).U)
                c.io.iaddr.expect((4*x).U)
                c.clock.step()
            } 
        }
    }
}

//def instruction_synthetic (rs1:Int ,rs2:Int ,rd:Int) = (0|51|(rd<<7)|(0<<12)|(rs1<<15)|(rs2<<20))

class DecodeSpec extends FreeSpec with ChiselScalatestTester with Matchers{
    
    def instruction_synthetic (rs1:Int ,rs2:Int ,rd:Int) = (0|51|(rd<<7)|(0<<12)|(rs1<<15)|(rs2<<20))
 
    "Decode should be OK " in {//scala.util.Random.nextInt(32)
        test(new DecodeDataPath).withAnnotations(Seq(WriteVcdAnnotation)) { c =>
            //use debug io to write reg
            c.io.debugIO.debug_wdata.poke(1.U)
            c.io.debugIO.debug_addr.poke(1.U)
            c.io.debugIO.debug_en.poke(true.B)
            c.clock.step()
            c.io.debugIO.debug_wdata.poke(2.U)
            c.io.debugIO.debug_addr.poke(2.U)
            c.io.debugIO.debug_en.poke(true.B)
            c.clock.step()
            //test read
            c.io.dec_inst.poke(instruction_synthetic(1,2,3).U)
            c.clock.step()
            // c.io.debug_addr.poke(3.U)
            // c.io.debug_rdata.expect(3.U)
            c.io.dectoexeIO.exe_op1_data.expect(1.U)
            c.io.dectoexeIO.exe_op2_data.expect(2.U)
            c.io.dectoexeIO.exe_wbaddr.expect(3.U)
            c.clock.step()
            //test write
            c.io.wbtodecIO.wb_rd_addr.poke(3.U)
            c.io.wbtodecIO.wb_rd_en.poke(true.B)
            c.io.wbtodecIO.wb_rd_data.poke(3.U)
            c.clock.step()
            c.io.debugIO.debug_addr.poke(3.U)
            c.io.debugIO.debug_rdata.expect(3.U)

            
        }
    }
}

// class DecodeSpecWave extends FlatSpec with Matchers{

//     "WaveformCounter" should "pass" in { 
//         Driver . execute ( Array ("−−generate−vcd−output " , "on") , () => new DeviceUnderTest () ) { c =>
//             new DecodeSpec(c)
        
//     }
// }


class ExecuteSpec extends FreeSpec with ChiselScalatestTester with Matchers{
    
    "Execute should be OK " in {//scala.util.Random.nextInt(32)
        test(new ExecuteDataPath).withAnnotations(Seq(WriteVcdAnnotation))  { c =>
        // val debug_rdata = Output(UInt(64.W))
        // val debug_wdata =Input(UInt(64.W))
        // val debug_en =Input(Bool())nnotations(Seq(WriteVcdAnnotation))  { c =>
            var x=0
            for (x <- 0 to 100)
            {
                val op1 = Random.nextInt(429496729)
                val op2 = Random.nextInt(429496729)
                val result = op1 + op2

                val addr = Random.nextInt(32)

                c.io.dectoexeIO.exe_wbaddr.poke(addr.U)

                c.io.dectoexeIO.exe_op1_data.poke(op1.U)
                c.io.dectoexeIO.exe_op2_data.poke(op2.U)

                c.clock.step()

                c.io.exetomemIO.mem_alu_out.expect(result.U)
                c.io.exetomemIO.mem_wbaddr.expect(addr.U)

               // println(c.io.mem_alu_out.peek())
                c.io.exetomemIO.mem_alu_out.peek()
            }
        }
    }
}

class MemorySpec extends FreeSpec with ChiselScalatestTester with Matchers{
    
    "Memory should be OK " in {//scala.util.Random.nextInt(32)
        test(new MemoryDataPath) { c =>

            var x = 0
            for (x <- 0 to 100)
            {
                val alu_out = Random.nextInt(429496729)
                val addr = Random.nextInt(32)
                
                

                c.io.exetomemIO.mem_alu_out.poke(alu_out.U)
                c.io.exetomemIO.mem_wbaddr.poke(addr.U)
                //c.io.mem_alu_out.peek()
                c.clock.step()

                c.io.wbtodecIO.wb_rd_data.expect(alu_out.U)
                c.io.wbtodecIO.wb_rd_addr.expect(addr.U)
            }
        }
    }
}

class TopSpec extends FreeSpec with ChiselScalatestTester with Matchers{
    
    "Top should be OK " in {//scala.util.Random.nextInt(32)
        test(new TopDataPath).withAnnotations(Seq(WriteVcdAnnotation))  { c =>
            def instruction_synthetic (rs1:Int ,rs2:Int ,rd:Int) = (0|51|(rd<<7)|(0<<12)|(rs1<<15)|(rs2<<20))
            
            var x = 0
            //c.io.dec_wen.poke(true.B)

            c.clock.step()

            for (x <- 0 to 32 )
            {
                c.io.debugIO.debug_wdata.poke(x.U)
                c.io.debugIO.debug_addr.poke(x.U)
                c.io.debugIO.debug_en.poke(true.B)
                c.clock.step()
            }
            for (x <- 0 to 100)
            {
                val rs1_addr = Random.nextInt(32)
                val rs2_addr = Random.nextInt(32)
                val rd_addr = Random.nextInt(32)

                //c.io.dec_inst.poke(instruction_synthetic(rs1_addr,rs2_addr,rd_addr).U)

                c.clock.step()

            }
            for (x <- 0 to 32)
            {
                c.io.debugIO.debug_addr.poke(x.U)
                c.clock.step()
            }
            
        }
    }
}



class Top2Spec extends FreeSpec with ChiselScalatestTester with Matchers{
    
    "Top should be OK " in {//scala.util.Random.nextInt(32)
        test(new Top2DataPath).withAnnotations(Seq(WriteVcdAnnotation))  { c =>
            def instruction_synthetic (rs1:Int ,imm:Int ,rd:Int) = (0|19|(rd<<7)|(0<<12)|(rs1<<15)|(imm<<20))
            
            var x = 0
            //c.io.dec_wen.poke(true.B)

            c.clock.step()

            for (x <- 0 to 32 )
            {
                c.io.debugIO.debug_wdata.poke(x.U)
                c.io.debugIO.debug_addr.poke(x.U)
                c.io.debugIO.debug_en.poke(true.B)
                c.clock.step()
            }
            for (x <- 0 to 100)
            {
                val rs1_addr = Random.nextInt(32)
                val imm = Random.nextInt(10)
                val rd_addr = Random.nextInt(32)

                c.io.dec_inst.poke((BigInt("00100093",16)).U)

                c.clock.step()

            }
            for (x <- 0 to 32)
            {
                c.io.debugIO.debug_addr.poke(x.U)
                c.clock.step()
            }
            
        }
    }
}










