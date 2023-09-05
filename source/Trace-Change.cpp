/**
 * 描述: Trace实现文件
 *
 */

#include"../include/Trace-Change.h"

namespace change
{
	const char* Trace::kSTR_NAME = "trace.txt";		/* 创建跟踪文件的默认名 */
	const char* Trace::kSTR_INFO = "Info    ";		/* 信息 */
	const char* Trace::kSTR_WARNING = "Warning ";	/* 警告 */
	const char* Trace::kSTR_ERROR = "Error   ";		/* 错误 */
	/**
	* 描述: 构造Trace对象, 创建跟踪文件
	*
	* file[in]: 欲创建的文件名
	*/
	Trace::Trace(const char * file)
	{
		if (NULL == file || NULL == (fp_ = fopen(file, "w")))
		{
			on_ = false;
			return;
		}

		if (0 != setvbuf(fp_, NULL, _IONBF, 0))
		{
			on_ = false;
			return;
		}

		THREAD_MUTEX_INIT(&mt_);
		on_ = true;
	}

	/**
	 * 描述: 获取跟踪开关状态
	 *
	 * 返回值: true打开, false关闭
	 */
	bool Trace::GetTraceStatus(void)
	{
		return on_;
	}

	/**
	 * 描述: 设置跟踪开关状态
	 *
	 * 返回值: true打开, false关闭
	 */
	void Trace::SetTraceStatus(bool on)
	{
		on_ = on;
	}

	/**
	 * 描述: 获取跟踪文件指针
	 *
	 * 返回值: 成功返回文件指针, 失败NULL
	 */
	FILE * Trace::GetTraceFile(void)
	{
		return fp_;
	}

	/**
	 * 描述: 获取枚举字符串
	 *
	 * level[in]: 错误等级
	 *
	 * 返回值: 成功返回常量字符串, 失败NULL
	 */
	const char * Trace::GetStrLevel(int level)
	{
		switch (level)
		{
		case kINFO:
			return kSTR_INFO;

		case kWARNING:
			return kSTR_WARNING;

		case kERROR:
			return kSTR_ERROR;
		}
		return NULL;
	}

	/**
	 * 描述: 获取本地时间
	 *
	 * time_str[out]: 保存本地时间字符串的缓冲区
	 *
	 * 返回值: 成功返回字符串, 失败NULL
	 */
	char * Trace::GetStrLocalTime(char * time_str, unsigned size)
	{
		if (24 > size)
		{
			return NULL;
		}

		time_t now_time = time(NULL);
		struct tm * now_tm = localtime(&now_time);

		sprintf(time_str, "%04d-%02d-%02d %02d:%02d:%02d  ", now_tm->tm_year + 1900, now_tm->tm_mon + 1, \
			now_tm->tm_mday, now_tm->tm_hour, now_tm->tm_min, now_tm->tm_sec);

		return time_str;
	}

	/**
	 * 描述: 从文件路径找出文件名, 辅助于_Trace函数
	 *
	 *file[in]: 文件路径
	 *
	 *返回值: 成功非NULL, 失败NULL
	 */
	static const char * TruncateFileName(const char * file)
	{
		if (NULL == file)
		{
			return NULL;
		}

		const char * tmp = file;
		for (; 0 != *file; ++file)
		{
			if ('\\' == file[0] || '/' == file[0])
			{
				tmp = file + 1;
			}
		}
		return tmp;
	}



	/**
	 * 描述: 打印跟踪信息到文件系统
	 *
	 * file[in]: 原文件名
	 *
	 * func[in]: 函数名
	 *
	 * line[in]: 行号
	 */
	void Trace::_Trace(const char * file, const char * func, unsigned line)
	{
		if (!on_)
		{
			return;
		}

		try
		{

			
			THREAD_MUTEX_LOCK(&mt_);

			char time_str[24];

			if (NULL != GetStrLocalTime(time_str, sizeof time_str) && (file = TruncateFileName(file)))
			{
				fprintf(fp_, "\n%s%-20s  %-20s  %-8u\n", time_str, file, func, line);
			}

			THREAD_MUTEX_UNLOCK(&mt_);
		}
		catch (...)
		{
			THREAD_MUTEX_UNLOCK(&mt_);
			on_ = false;
		}
	}

	/**
	 * 描述: 打印输入信息到文件系统
	 *
	 * level[in]: 错误等级
	 *
	 * format[in]: 格式化字符串
	 */
	void Trace::_Print(int level, const char * format, ...)
	{
		if (!on_)
		{
			return;
		}

		try
		{
			THREAD_MUTEX_LOCK(&mt_);

			va_list list;
			va_start(list, format);

			char buffer[4096];
			strcpy(buffer, GetStrLevel(level));
			int len = strlen(buffer);
			vsnprintf(buffer + len, sizeof buffer - len - 1, format, list);

			len = strlen(buffer);
			buffer[len] = '\n';
			len += 1;
			if (fwrite(buffer, sizeof(char), len, fp_) != len)
			{
				on_ = false;
			}

			THREAD_MUTEX_UNLOCK(&mt_);
		}
		catch (...)
		{
			THREAD_MUTEX_UNLOCK(&mt_);
		}
	}

	/**
	 * 描述: 析构函数
	 *
	 */
	Trace::~Trace(void)
	{
		fclose(fp_);
		THREAD_MUTEX_DESTROY(&mt_);
	}

	Trace trace;		/* 唯一的对象 */
}
