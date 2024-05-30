; ModuleID = 'module'
source_filename = "module"

define i32 @main() {
mainEntry:
  %a = alloca i32, align 4
  store i32 0, i32* %a, align 4
  %count = alloca i32, align 4
  store i32 0, i32* %count, align 4
  %a1 = load i32, i32* %a, align 4
  %cmptmp = icmp sgt i32 %a1, 0
  %zexttmp = zext i1 %cmptmp to i32
  %finalcmp = icmp ne i32 0, %zexttmp
  %result = alloca i1, align 1
  br i1 %finalcmp, label %or.true0, label %or.false0

true0:                                            ; preds = %or.end0
  %a7 = load i32, i32* %a, align 4
  ret i32 %a7
  br label %end0

false0:                                           ; preds = %or.end0
  %count8 = load i32, i32* %count, align 4
  ret i32 %count8
  br label %end0

end0:                                             ; preds = %false0, %true0

or.true0:                                         ; preds = %mainEntry
  store i1 true, i1* %result, align 1
  br label %or.end0

or.false0:                                        ; preds = %mainEntry
  %count2 = load i32, i32* %count, align 4
  %cmptmp3 = icmp sgt i32 %count2, 0
  %zexttmp4 = zext i1 %cmptmp3 to i32
  %finalcmp5 = icmp ne i32 0, %zexttmp4
  store i1 %finalcmp5, i1* %result, align 1
  br label %or.end0

or.end0:                                          ; preds = %or.false0, %or.true0
  %result6 = load i1, i1* %result, align 1
  br i1 %result6, label %true0, label %false0
}
