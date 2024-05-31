; ModuleID = 'module'
source_filename = "module"

define i32 @main() {
mainEntry:
  %a = alloca i32, align 4
  store i32 0, i32* %a, align 4
  %count = alloca i32, align 4
  store i32 0, i32* %count, align 4
  br label %whileCondition0

whileCondition0:                                  ; preds = %end019, %mainEntry
  %a1 = load i32, i32* %a, align 4
  %cmptmp = icmp sle i32 %a1, 0
  %zexttmp = zext i1 %cmptmp to i32
  %finalcmp = icmp ne i32 0, %zexttmp
  br i1 %finalcmp, label %whileBody0, label %end0

whileBody0:                                       ; preds = %whileCondition0
  %a2 = load i32, i32* %a, align 4
  %subtmp = sub i32 %a2, 1
  store i32 %subtmp, i32* %a, align 4
  %count3 = load i32, i32* %count, align 4
  %addtmp = add i32 %count3, 1
  store i32 %addtmp, i32* %count, align 4
  br label %whileCondition04

end0:                                             ; preds = %true0, %whileCondition0
  %count24 = load i32, i32* %count, align 4
  ret i32 %count24

whileCondition04:                                 ; preds = %whileBody05, %whileBody0
  %a7 = load i32, i32* %a, align 4
  %cmptmp8 = icmp sle i32 %a7, 0
  %zexttmp9 = zext i1 %cmptmp8 to i32
  %finalcmp10 = icmp ne i32 0, %zexttmp9
  %result = alloca i1, align 1
  br i1 %finalcmp10, label %and.true0, label %and.false0

whileBody05:                                      ; preds = %and.end0
  %count16 = load i32, i32* %count, align 4
  %multmp = mul i32 %count16, 2
  store i32 %multmp, i32* %count, align 4
  %a17 = load i32, i32* %a, align 4
  %subtmp18 = sub i32 %a17, 1
  store i32 %subtmp18, i32* %a, align 4
  br label %whileCondition04

end06:                                            ; preds = %and.end0
  %a20 = load i32, i32* %a, align 4
  %cmptmp21 = icmp slt i32 %a20, -20
  %zexttmp22 = zext i1 %cmptmp21 to i32
  %finalcmp23 = icmp ne i32 0, %zexttmp22
  br i1 %finalcmp23, label %true0, label %false0

and.true0:                                        ; preds = %whileCondition04
  %a11 = load i32, i32* %a, align 4
  %cmptmp12 = icmp sgt i32 %a11, -3
  %zexttmp13 = zext i1 %cmptmp12 to i32
  %finalcmp14 = icmp ne i32 0, %zexttmp13
  store i1 %finalcmp14, i1* %result, align 1
  br label %and.end0

and.false0:                                       ; preds = %whileCondition04
  store i1 false, i1* %result, align 1
  br label %and.end0

and.end0:                                         ; preds = %and.false0, %and.true0
  %result15 = load i1, i1* %result, align 1
  br i1 %result15, label %whileBody05, label %end06

true0:                                            ; preds = %end06
  br label %end0
  br label %end019

false0:                                           ; preds = %end06
  br label %end019

end019:                                           ; preds = %false0, %true0
  br label %whileCondition0
}
