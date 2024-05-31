; ModuleID = 'module'
source_filename = "module"

@i = global i32 0
@j = global i32 1
@k = global i32 2

define i32 @main() {
mainEntry:
  %a = alloca i32, align 4
  store i32 0, i32* %a, align 4
  %count = alloca i32, align 4
  store i32 0, i32* %count, align 4
  br label %whileCondition0

whileCondition0:                                  ; preds = %end021, %mainEntry
  %a1 = load i32, i32* %a, align 4
  %cmptmp = icmp sle i32 %a1, 0
  %zexttmp = zext i1 %cmptmp to i32
  %finalcmp = icmp ne i32 0, %zexttmp
  br i1 %finalcmp, label %whileBody0, label %end0

whileBody0:                                       ; preds = %whileCondition0
  %b = alloca i32, align 4
  store i32 0, i32* %b, align 4
  %a2 = load i32, i32* %a, align 4
  %subtmp = sub i32 %a2, 1
  %b3 = load i32, i32* %b, align 4
  %addtmp = add i32 %subtmp, %b3
  store i32 %addtmp, i32* %a, align 4
  %count4 = load i32, i32* %count, align 4
  %addtmp5 = add i32 %count4, 1
  store i32 %addtmp5, i32* %count, align 4
  br label %whileCondition06

end0:                                             ; preds = %true0, %whileCondition0
  %count28 = load i32, i32* %count, align 4
  ret i32 %count28

whileCondition06:                                 ; preds = %whileBody07, %whileBody0
  %a9 = load i32, i32* %a, align 4
  %cmptmp10 = icmp sle i32 %a9, 0
  %zexttmp11 = zext i1 %cmptmp10 to i32
  %finalcmp12 = icmp ne i32 0, %zexttmp11
  %result = alloca i1, align 1
  br i1 %finalcmp12, label %and.true0, label %and.false0

whileBody07:                                      ; preds = %and.end0
  %count18 = load i32, i32* %count, align 4
  %multmp = mul i32 %count18, 2
  store i32 %multmp, i32* %count, align 4
  %a19 = load i32, i32* %a, align 4
  %subtmp20 = sub i32 %a19, 1
  store i32 %subtmp20, i32* %a, align 4
  br label %whileCondition06

end08:                                            ; preds = %and.end0
  %a22 = load i32, i32* %a, align 4
  %cmptmp23 = icmp slt i32 %a22, -20
  %zexttmp24 = zext i1 %cmptmp23 to i32
  %finalcmp25 = icmp ne i32 0, %zexttmp24
  br i1 %finalcmp25, label %true0, label %false0

and.true0:                                        ; preds = %whileCondition06
  %a13 = load i32, i32* %a, align 4
  %cmptmp14 = icmp sgt i32 %a13, -3
  %zexttmp15 = zext i1 %cmptmp14 to i32
  %finalcmp16 = icmp ne i32 0, %zexttmp15
  store i1 %finalcmp16, i1* %result, align 1
  br label %and.end0

and.false0:                                       ; preds = %whileCondition06
  store i1 false, i1* %result, align 1
  br label %and.end0

and.end0:                                         ; preds = %and.false0, %and.true0
  %result17 = load i1, i1* %result, align 1
  br i1 %result17, label %whileBody07, label %end08

true0:                                            ; preds = %end08
  br label %end0
  br label %end021

false0:                                           ; preds = %end08
  br label %end021

end021:                                           ; preds = %false0, %true0
  %b26 = load i32, i32* %b, align 4
  %addtmp27 = add i32 %b26, 1
  store i32 %addtmp27, i32* %b, align 4
  br label %whileCondition0
}
